package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipminiproject.data.model.CartItem
import com.example.internshipminiproject.data.model.CartSummary
import com.example.internshipminiproject.data.model.User
import com.example.internshipminiproject.data.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.get

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    fun addToCart(cartItem: CartItem) {
        val userId = currentUserId ?: return
        val userDoc = usersCollection.document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            user?.let {
                val updatedCart = it.cart.toMutableList()

                val existing = updatedCart.find { existingItem ->
                    existingItem.productId == cartItem.productId &&
                            existingItem.selectedSize == cartItem.selectedSize &&
                            existingItem.selectedColor == cartItem.selectedColor &&
                            existingItem.price == cartItem.price
                }

                if (existing != null) {
                    existing.quantity = cartItem.quantity
                } else {
                    updatedCart.add(cartItem)
                }

                userDoc.update("cart", updatedCart)
                    .addOnSuccessListener {
                        Log.d("Cart", "Item added/updated successfully")
                        _cartItems.value = updatedCart
                    }
                    .addOnFailureListener { e ->
                        Log.e("Cart", "Error updating cart: ${e.message}")
                    }
            }
        }.addOnFailureListener {
            Log.e("Cart", "Failed to fetch user: ${it.message}")
        }
    }

    fun fetchCartItems() {
        val userId = currentUserId ?: return
        val userDoc = usersCollection.document(userId)

        userDoc.get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _cartItems.value = user?.cart ?: emptyList()
            }
            .addOnFailureListener {
                Log.e("Cart", "Failed to fetch cart: ${it.message}")
                _cartItems.value = emptyList()
            }
    }

    fun fetchCartSummaryById(
        productId: String,
        onSuccess: (CartSummary) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val product = RetrofitInstance.api.getCartSummaryById(productId)
                onSuccess(product)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userDoc = usersCollection.document(currentUser.uid)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            user?.let {
                val updatedCart = it.cart.map {
                    if (it.productId == cartItem.productId &&
                        it.selectedColor == cartItem.selectedColor &&
                        it.selectedSize == cartItem.selectedSize &&
                        it.price == cartItem.price
                    ) it.copy(quantity = newQuantity) else it
                }
                userDoc.update("cart", updatedCart)
                    .addOnSuccessListener {
                        _cartItems.value = updatedCart
                    }
            }
        }
    }



    fun removeFromCart(cartItem: CartItem) {
        val userId = currentUserId ?: return
        val userDoc = usersCollection.document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            user?.let {
                val updatedCart = it.cart.filterNot { item ->
                    item.productId == cartItem.productId &&
                            item.selectedSize == cartItem.selectedSize &&
                            item.selectedColor == cartItem.selectedColor &&
                            item.price == cartItem.price
                }

                userDoc.update("cart", updatedCart)
                    .addOnSuccessListener {
                        Log.d("Cart", "Item removed successfully")
                        _cartItems.value = updatedCart
                    }
                    .addOnFailureListener {
                        Log.e("Cart", "Failed to remove item: ${it.message}")
                    }
            }
        }.addOnFailureListener {
            Log.e("Cart", "Failed to fetch user for removal: ${it.message}")
        }
    }

    fun clearCart() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userDoc = usersCollection.document(currentUser.uid)

        userDoc.update("cart", emptyList<CartItem>())
            .addOnSuccessListener {
                Log.d("Cart", "✅ Cart cleared successfully")
                _cartItems.value = emptyList()
            }
            .addOnFailureListener {
                Log.e("Cart", "❌ Failed to clear cart: ${it.message}")
            }
    }

}
