package com.example.internshipminiproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internshipminiproject.data.model.Address
import com.example.internshipminiproject.data.model.CartItem
import com.example.internshipminiproject.data.model.Order
import com.example.internshipminiproject.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.util.UUID

@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun placeOrder(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        val userDoc = usersCollection.document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            if (user == null) {
                onFailure(Exception("User not found"))
                return@addOnSuccessListener
            }

            val cartItems = user.cart
            val defaultAddress = user.addresses.find { it.default }

            if (cartItems.isEmpty()) {
                onFailure(Exception("Cart is empty"))
                return@addOnSuccessListener
            }

            if (defaultAddress == null) {
                onFailure(Exception("No default address selected"))
                return@addOnSuccessListener
            }

            val orderId = UUID.randomUUID().toString()
            val productIds = cartItems.map { it.productId }
            val totalAmount = cartItems.sumOf { it.price * it.quantity }

            val newOrder = Order(
                orderId = orderId,
                productIds = productIds,
                totalAmount = totalAmount.toDouble(),
                address = defaultAddress
            )

            val updatedOrders = user.orders.toMutableList().apply { add(newOrder) }

            userDoc.update(
                mapOf(
                    "orders" to updatedOrders,
                    "cart" to emptyList<Map<String, Any>>() // Clear cart
                )
            ).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure(it)
            }

        }.addOnFailureListener {
            onFailure(it)
        }
    }


    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    fun fetchUserOrders() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _orders.value = user?.orders ?: emptyList()
            }
            .addOnFailureListener {
                _orders.value = emptyList()
            }
    }

    fun getOrderById(orderId: String, onResult: (Order?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                val order = user?.orders?.find { it.orderId == orderId }
                onResult(order)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

}
