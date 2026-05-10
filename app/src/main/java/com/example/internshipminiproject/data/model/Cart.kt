package com.example.internshipminiproject.data.model

data class CartSummary(
    val id: String = "",
    val title: String = "",
    val price: Double = 0.0,
    val thumbnail: String = "",
    val minimumOrderQuantity: Int = 1,
    val tags: List<String> ,
    val category: String = ""
)