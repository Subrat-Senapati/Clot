package com.example.internshipminiproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internshipminiproject.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _wishlist = MutableLiveData<List<String>>()
    val wishlist: LiveData<List<String>> = _wishlist


    // 🔹 Fetch user's wishlist from Firestore
    fun fetchWishlist() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _wishlist.value = user?.wishlist ?: emptyList()
            }
    }


    // 🔹 Add or remove product ID from wishlist
    fun toggleWishlistItem(productId: String) {
        val userId = auth.currentUser?.uid ?: return
        val currentList = _wishlist.value?.toMutableList() ?: mutableListOf()

        if (currentList.contains(productId)) {
            currentList.remove(productId)
        } else {
            currentList.add(productId)
        }

        firestore.collection("users").document(userId)
            .update("wishlist", currentList)
            .addOnSuccessListener {
                _wishlist.value = currentList
            }
    }

    // 🔹 Check if a product is already in the wishlist
    fun isInWishlist(productId: String): Boolean {
        return _wishlist.value?.contains(productId) == true
    }
}
