package com.example.internshipminiproject.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val category: String,
    val price: Double,
    val thumbnail: String,
)


@Serializable
data class ProductDetails(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val tags: List<String>,
    val brand: String,
    val sku: String,
    val weight: Int,
//    val dimensions: Dimensions,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
//    val reviews: List<Review>,
    val returnPolicy: String,
    val minimumOrderQuantity: Int,
//    val meta: Meta,
    val thumbnail: String,
    val images: List<String>
)


@Serializable
data class Categories(
    val slug: String,
    val name: String,
    val url: String,
)

