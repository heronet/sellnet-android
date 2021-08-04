package com.heronet.sellnet.web

import com.heronet.sellnet.model.AuthData
import com.heronet.sellnet.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface SellnetApi {
    @GET("products/all")
    suspend fun getProducts(
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("name") name: String? = null,
        @Query("city") city: String? = null,
        @Query("division") division: String? = null,
        @Query("category") category: String? = null,
        @Query("sortParam") sortParam: String? = null,
        @Query("sellerId") sellerId: String? = null
    ): ApiResponse<List<Product>>

    @POST("products")
    @Multipart
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part photos: List<MultipartBody.Part>,
        @Header("Authorization") authorization: String
    )

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: String): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Header("Authorization") authorization: String, @Path("id") id: String)

    @POST("account/login")
    suspend fun login(@Body loginDto: LoginDto): AuthData

    @POST("account/register")
    suspend fun register(@Body registerDto: RegisterDto): AuthData

    @POST("account/refresh")
    suspend fun refreshToken(@Header("Authorization") authorization: String, @Body authData: AuthData): AuthData

    @GET("utilities/locations")
    suspend fun getLocations(): Location


}