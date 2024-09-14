package com.example.easypropertydealer
import com.example.easypropertydealer.Property
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PropertyApiService {

    @GET("api/Property")
    suspend fun getProperties(): List<Property>

    @POST("api/Property")
    suspend fun addProperty(@Body property: Property): Response<Property>

    @PUT("api/Property/{id}")
    suspend fun updateProperty(
        @Path("id") id: Int,
        @Body property: Property
    ): Response<Property>

    @DELETE("api/Property/{id}")
    suspend fun deleteProperty(@Path("id") id: Int): Response<Unit>
}
