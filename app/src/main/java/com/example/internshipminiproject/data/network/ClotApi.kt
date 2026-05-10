package com.example.internshipminiproject.data.network

import com.example.internshipminiproject.data.model.CartSummary
import com.example.internshipminiproject.data.model.Categories
import com.example.internshipminiproject.data.model.ProductDetails
import com.example.internshipminiproject.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ClotApi {
    @GET("products")
    suspend fun getProductList(
        @Query("limit") limit: Int = 0
    ): ProductResponse

    @GET("products/categories")
    suspend fun getCategoriesList(): List<Categories>

    @GET("products/category/{slug}")
    suspend fun getProductsByCategory(
        @Path("slug") slug: String,
        @Query("limit") limit: Int = 0
    ): ProductResponse

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): ProductDetails

    @GET("products/{id}")
    suspend fun getCartSummaryById(
        @Path("id") productId: String
    ): CartSummary

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductResponse
}