import com.example.easypropertydealer.Deal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DealApiService {

    @GET("api/Deal")
    suspend fun getDeals(): List<Deal>

    @POST("api/Deal")
    suspend fun addDeal(@Body deal: Deal): Response<Deal>

    @PUT("api/Deal/{id}")
    suspend fun updateDeal(
        @Path("id") id: Int,
        @Body deal: Deal
    ): Response<Deal>

    @DELETE("api/Deal/{id}")
    suspend fun deleteDeal(@Path("id") id: Int): Response<Unit>
}
