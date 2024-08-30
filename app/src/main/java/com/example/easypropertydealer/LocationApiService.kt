import com.example.easypropertydealer.Location
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LocationApiService {
    @GET("api/Location")
    suspend fun getLocations(): List<Location>

    @POST("api/Location")
    suspend fun addLocation(@Body location: Location): Response<Location>

    @PUT("api/Location/{id}")
    suspend fun updateLocation(
        @Path("id") id: Int,
        @Body location: Location
    ): Response<Location>

    @DELETE("api/Location/{id}")
    suspend fun deleteLocation(@Path("id") id: Int): Response<Unit>
}
