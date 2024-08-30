package com.example.easypropertydealer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MySQLiteHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {

        //notes table query
        val createNotesTableQuery = ("CREATE TABLE " + NOTES_TABLE_NAME + " ("
                + NOTE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONTENT_COL + " TEXT, " +
                CREATION_DATE_COL + " TEXT" + ")")

        //contacts table query
        val createContactsTableQuery = ("CREATE TABLE $CONTACTS_TABLE_NAME ("
                + "$CONTACT_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NAME_COL TEXT NOT NULL, " +
                "$MOBILE_NO_COL TEXT NOT NULL, " +
                "$ADDRESS_COL TEXT, " +
                "$DEALER_COL INTEGER, " +  // 0 for No, 1 for Yes
                "$INVESTOR_COL INTEGER, " +  // 0 for No, 1 for Yes
                "$COMPANY_NAME_COL TEXT, " +
                "$PERSON_DETAILS_COL TEXT, " +
                "$EMAIL_COL TEXT, " +
                "$WEBSITE_COL TEXT)")

        // Location table query
        val createLocationTableQuery = ("CREATE TABLE $LOCATION_TABLE_NAME ("
                + "$LOCATION_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COUNTRY_COL TEXT, "
                + "$STATE_COL TEXT, "
                + "$CITY_COL TEXT, "
                + "$LOCATION_NAME_COL TEXT)")

        db.execSQL(createNotesTableQuery)
        db.execSQL(createContactsTableQuery)
        db.execSQL(createLocationTableQuery)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            // Drop tables that might not exist in older versions
            db.execSQL("DROP TABLE IF EXISTS $NOTES_TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $CONTACTS_TABLE_NAME")

            // Create tables again
            onCreate(db)
        }

        // Check if the Location table exists, if not, create it
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='$LOCATION_TABLE_NAME'",
            null
        )
        if (cursor.count == 0) {
            cursor.close()
            // Location table does not exist, create it
            val createLocationTableQuery = ("CREATE TABLE $LOCATION_TABLE_NAME ("
                    + "$LOCATION_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COUNTRY_COL TEXT, "
                    + "$STATE_COL TEXT, "
                    + "$CITY_COL TEXT, "
                    + "$LOCATION_NAME_COL TEXT)")
            db.execSQL(createLocationTableQuery)
        } else {
            cursor.close()
        }
    }

    //insert method of Contact table
    fun insertContact(
        name: String,
        mobileNo: String,
        address: String?,
        dealer: Boolean,
        investor: Boolean,
        companyName: String?,
        personDetails: String?,
        email: String?,
        website: String?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(MOBILE_NO_COL, mobileNo)
            put(ADDRESS_COL, address)
            put(DEALER_COL, if (dealer) 1 else 0)
            put(INVESTOR_COL, if (investor) 1 else 0)
            put(COMPANY_NAME_COL, companyName)
            put(PERSON_DETAILS_COL, personDetails)
            put(EMAIL_COL, email)
            put(WEBSITE_COL, website)
        }
        val id = db.insert(CONTACTS_TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Method to update a contact in the Contacts table
    fun updateContact(
        contactId: Int,
        name: String,
        mobileNo: String,
        address: String?,
        dealer: Boolean,
        investor: Boolean,
        companyName: String?,
        personDetails: String?,
        email: String?,
        website: String?
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(MOBILE_NO_COL, mobileNo)
            put(ADDRESS_COL, address)
            put(DEALER_COL, if (dealer) 1 else 0)
            put(INVESTOR_COL, if (investor) 1 else 0)
            put(COMPANY_NAME_COL, companyName)
            put(PERSON_DETAILS_COL, personDetails)
            put(EMAIL_COL, email)
            put(WEBSITE_COL, website)
        }
        val result = db.update(CONTACTS_TABLE_NAME, values, "$CONTACT_ID_COL = ?", arrayOf(contactId.toString()))
        db.close()
        return result
    }

    // Method to delete a contact from the Contacts table
    fun deleteContact(contactId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(CONTACTS_TABLE_NAME, "$CONTACT_ID_COL = ?", arrayOf(contactId.toString()))
        db.close()
        return result
    }





    // Method to insert a new note into the Notes table
    fun insertNote(content: String, creationDate: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(CONTENT_COL, content)
            put(CREATION_DATE_COL, creationDate)
        }
        val id = db.insert(NOTES_TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Method to retrieve all contacts from the database
    fun getAllContacts(): List<Contact> {
        val db: SQLiteDatabase = this.readableDatabase
        val projection = arrayOf(
            CONTACT_ID_COL,
            NAME_COL,
            MOBILE_NO_COL,
            ADDRESS_COL,
            DEALER_COL,
            INVESTOR_COL,
            COMPANY_NAME_COL,
            PERSON_DETAILS_COL,
            EMAIL_COL,
            WEBSITE_COL
        )
        val cursor = db.query(
            CONTACTS_TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val contactsList = mutableListOf<Contact>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(CONTACT_ID_COL))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))
            val mobileNo = cursor.getString(cursor.getColumnIndexOrThrow(MOBILE_NO_COL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS_COL))
            val dealer = cursor.getInt(cursor.getColumnIndexOrThrow(DEALER_COL)) == 1
            val investor = cursor.getInt(cursor.getColumnIndexOrThrow(INVESTOR_COL)) == 1
            val companyName = cursor.getString(cursor.getColumnIndexOrThrow(COMPANY_NAME_COL))
            val personDetails = cursor.getString(cursor.getColumnIndexOrThrow(PERSON_DETAILS_COL))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_COL))
            val website = cursor.getString(cursor.getColumnIndexOrThrow(WEBSITE_COL))
            val contact = Contact(id, name, mobileNo, address, dealer, investor, companyName, personDetails, email, website)
            contactsList.add(contact)
        }
        cursor.close()
        db.close()
        return contactsList
    }

    // Method to retrieve all notes from the database
    fun getAllNotes(): List<Note> {
        val db: SQLiteDatabase = this.readableDatabase
        val projection = arrayOf(
            NOTE_ID_COL,
            CONTENT_COL,
            CREATION_DATE_COL
        )
        val cursor: Cursor = db.query(
            NOTES_TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val notesList = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(NOTE_ID_COL))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_COL))
            val creationDate = cursor.getString(cursor.getColumnIndexOrThrow(CREATION_DATE_COL))
            val note = Note(id, content, creationDate)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }


    // Method to delete a note from the Notes table
    fun deleteNote(noteId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(NOTES_TABLE_NAME, "$NOTE_ID_COL = ?", arrayOf(noteId.toString()))
        db.close()
        return result
    }

    // Method to update a note in the Notes table
    fun updateNote(noteId: Int, newContent: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(CONTENT_COL, newContent)
        }
        val result = db.update(NOTES_TABLE_NAME, values, "$NOTE_ID_COL = ?", arrayOf(noteId.toString()))
        db.close()
        return result
    }
    // Insert method for Location table
    fun insertLocation(location: Location): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COUNTRY_COL, location.country)
            put(STATE_COL, location.state)
            put(CITY_COL, location.city)
            put(LOCATION_NAME_COL, location.locationName)
        }
        val id = db.insert(LOCATION_TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Update method for Location table
    fun updateLocation(location: Location): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COUNTRY_COL, location.country)
            put(STATE_COL, location.state)
            put(CITY_COL, location.city)
            put(LOCATION_NAME_COL, location.locationName)
        }
        val result = db.update(LOCATION_TABLE_NAME, values, "$LOCATION_ID_COL = ?", arrayOf(location.id.toString()))
        db.close()
        return result
    }

    // Delete method for Location table
    fun deleteLocation(locationId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(LOCATION_TABLE_NAME, "$LOCATION_ID_COL = ?", arrayOf(locationId.toString()))
        db.close()
        return result
    }

    // Method to retrieve all locations from the Location table
    fun getAllLocations(): List<Location> {
        val db = this.readableDatabase
        val cursor = db.query(
            LOCATION_TABLE_NAME,
            arrayOf(LOCATION_ID_COL, COUNTRY_COL, STATE_COL, CITY_COL, LOCATION_NAME_COL),
            null, null, null, null, null
        )

        val locationList = mutableListOf<Location>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(LOCATION_ID_COL))
            val country = cursor.getString(cursor.getColumnIndexOrThrow(COUNTRY_COL))
            val state = cursor.getString(cursor.getColumnIndexOrThrow(STATE_COL))
            val city = cursor.getString(cursor.getColumnIndexOrThrow(CITY_COL))
            val locationName = cursor.getString(cursor.getColumnIndexOrThrow(LOCATION_NAME_COL))
            val location = Location(id, country, state, city, locationName)
            locationList.add(location)
        }
        cursor.close()
        db.close()
        return locationList
    }

    companion object {
        private const val DATABASE_NAME = "MyDataBase"
        private const val DATABASE_VERSION = 3

        const val NOTES_TABLE_NAME = "notes_table"
        const val NOTE_ID_COL = "note_id"
        const val CONTENT_COL = "content"
        const val CREATION_DATE_COL = "creation_date"

        const val CONTACTS_TABLE_NAME = "contacts_table"
        const val CONTACT_ID_COL = "contact_id"
        const val NAME_COL = "name"
        const val MOBILE_NO_COL = "mobile_no"
        const val ADDRESS_COL = "address"
        const val DEALER_COL = "dealer"
        const val INVESTOR_COL = "investor"
        const val COMPANY_NAME_COL = "company_name"
        const val PERSON_DETAILS_COL = "person_details"
        const val EMAIL_COL = "email"
        const val WEBSITE_COL = "website"


        const val LOCATION_TABLE_NAME = "location_table"
        const val LOCATION_ID_COL = "location_id"
        const val COUNTRY_COL = "country"
        const val STATE_COL = "state"
        const val CITY_COL = "city"
        const val LOCATION_NAME_COL = "location_name"
    }
}