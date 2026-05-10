package com.example.internshipminiproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internshipminiproject.data.model.NotificationItem
import com.example.internshipminiproject.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableLiveData<List<NotificationItem>>()
    val notifications: LiveData<List<NotificationItem>> = _notifications

    fun fetchNotifications() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                _notifications.value = user?.notifications?.reversed() ?: emptyList()
            }
            .addOnFailureListener {
                _notifications.value = emptyList()
            }
    }

    fun markAllAsSeen() {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { doc ->
            val user = doc.toObject(User::class.java)
            val updatedNotifications = user?.notifications?.map {
                it.copy(seen = true)
            } ?: return@addOnSuccessListener

            userDoc.update("notifications", updatedNotifications)
                .addOnSuccessListener {
                    _notifications.value = updatedNotifications
                }
        }
    }
}
