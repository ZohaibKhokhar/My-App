package com.example.easypropertydealer

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReminderApiService {

    @GET("api/Reminder")
    suspend fun getReminders(): List<Reminder>

    @POST("api/Reminder")
    suspend fun addReminder(@Body reminder: Reminder): Response<Reminder>

    @PUT("api/Reminder/{id}")
    suspend fun updateReminder(
        @Path("id") id: Int,
        @Body reminder: Reminder
    ): Response<Reminder>

    @DELETE("api/Reminder/{id}")
    suspend fun deleteReminder(@Path("id") id: Int): Response<Unit>
}
