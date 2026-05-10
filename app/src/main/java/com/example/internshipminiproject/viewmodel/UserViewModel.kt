package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internshipminiproject.data.model.CartItem
import com.example.internshipminiproject.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // ─── Create User ───────────────────────────────────────────────────────────────
    fun createUserData(
        id: String,
        fname: String,
        lname: String,
        email: String,
        gender: String = "",
        agerange: String = "",
        onResult: (Boolean, String?) -> Unit
    ) {
        if (id.isBlank()) {
            onResult(false, "User ID missing")
            return
        }

        val user = User(
            id = id,
            firstname = fname,
            lastname = lname,
            email = email,
            gender = gender,
            agerange = agerange
        )

        usersCollection.document(id).set(user)
            .addOnSuccessListener {
                Log.d("UserViewModel", "✅ User created: $user")
                onResult(true, null)
            }
            .addOnFailureListener { error ->
                Log.e("UserViewModel", "❌ Error creating user: ${error.message}")
                onResult(false, error.message)
            }
    }

    // ─── Update Additional Fields ─────────────────────────────────────────────────
    fun updateUserAdditionalData(
        userId: String,
        gender: String,
        agerange: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (userId.isBlank()) {
            onResult(false, "User ID is missing")
            return
        }

        val updates = mapOf(
            "gender" to gender,
            "agerange" to agerange
        )

        usersCollection.document(userId).update(updates)
            .addOnSuccessListener {
                Log.d("UserViewModel", "✅ Gender & Age updated for $userId")
                onResult(true, null)
            }
            .addOnFailureListener { error ->
                Log.e("UserViewModel", "❌ Failed to update: ${error.message}")
                onResult(false, error.message)
            }
    }

    // ─── Update Profile ───────────────────────────────────────────────────────────
    fun updateUserProfile(
        firstname: String,
        lastname: String,
        gender: String,
        ageRange: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val userId = currentUserId ?: return

        val updates = mapOf(
            "firstname" to firstname,
            "lastname" to lastname,
            "gender" to gender,
            "agerange" to ageRange
        )

        usersCollection.document(userId).update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    // ─── Fetch User ────────────────────────────────────────────────────────────────
    fun fetchCurrentUserData(userId: String) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fetchedUser = document.toObject(User::class.java)
                    _user.value = fetchedUser
                    Log.d("UserViewModel", "✅ User fetched: $fetchedUser")
                } else {
                    Log.w("UserViewModel", "⚠️ User document does not exist")
                    _user.value = null
                }
            }
            .addOnFailureListener {
                Log.e("UserViewModel", "❌ Failed to fetch user: ${it.message}")
                _user.value = null
            }
    }
}
