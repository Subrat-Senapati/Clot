package com.example.internshipminiproject.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val gender: String = "",
    val agerange: String = "",
    val image: String = "",

    val wishlist: List<String> = emptyList(),               // List of product IDs
    val cart: List<CartItem> = emptyList(),                 // List of cart items
    val orders: List<Order> = emptyList(),                  // List of order summaries
    val notifications: List<NotificationItem> = emptyList(),// App notifications
    val addresses: List<Address> = emptyList()              // Optional shipping addresses
)

@Serializable
data class CartItem(
    val productId: String = "",
    var quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val price: Int = 0
)

@Serializable
data class Order(
    val orderId: String = "",
    val productIds: List<String> = emptyList(),
    val orderDate: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val status: String = "Pending",
    val address: Address = Address()
)


@Serializable
data class Address(
    val addressId : String = "",
    val name: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val phoneNumber: String = "",
    val default: Boolean = false
)

@Serializable
data class NotificationItem(
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
)