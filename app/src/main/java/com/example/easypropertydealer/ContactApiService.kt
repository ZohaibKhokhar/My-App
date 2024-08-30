import com.example.easypropertydealer.Contact
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactApiService {

    @GET("api/Contact")
    suspend fun getContacts(): List<Contact>

    @POST("api/Contact")
    suspend fun addContact(@Body contact: Contact): Response<Contact>

    @PUT("api/Contact/{id}")
    suspend fun updateContact(
        @Path("id") id: Int,
        @Body contact: Contact
    ): Response<Contact>

    @DELETE("api/Contact/{id}")
    suspend fun deleteContact(@Path("id") id: Int): Response<Unit>
}
