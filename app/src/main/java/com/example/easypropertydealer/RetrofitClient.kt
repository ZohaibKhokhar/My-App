package com.example.easypropertydealer

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

object RetrofitClient {
    private const val BASE_URL = "https://www.universal-tutorial.com/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {

    // Fetch Auth Token
    @GET("getaccesstoken")
    suspend fun getAuthToken(
        @Header("api-token") apiToken: String = "rCepCCS7diTQx2afPZ1tFLKDP75OGZT7mZZnInXRbT9eXNVLX8RZnuKDGz1QOo_m8sc",
        @Header("user-email") email: String = "zohaib.khokhar4056@gmail.com"
    ): Response<AuthResponse>

    // Fetch Countries
    @GET("countries")
    suspend fun getCountries(
        @Header("Authorization") token: String
    ): Response<List<Country>>

    // Fetch States for a Country
    @GET("states/{country}")
    suspend fun getStates(
        @Path("country") country: String,
        @Header("Authorization") token: String
    ): Response<List<State>>

    // Fetch Cities for a State
    @GET("cities/{state}")
    suspend fun getCities(
        @Path("state") state: String,
        @Header("Authorization") token: String
    ): Response<List<City>>
}
