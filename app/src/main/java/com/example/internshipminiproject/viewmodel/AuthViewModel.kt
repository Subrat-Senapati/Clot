package com.example.internshipminiproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipminiproject.data.datastore.UserPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPrefs: UserPreference,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val currentUser = auth.currentUser

        viewModelScope.launch {
            val storedUserId = userPrefs.getUserId()

            if (currentUser == null || storedUserId.isNullOrBlank()) {
                _authState.value = AuthState.Unauthenticated
                return@launch
            }

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(storedUserId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Unauthenticated
                    }
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error("Error checking user in Firestore")
                }
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    viewModelScope.launch {
                        userPrefs.saveUserId(uid)
                        _authState.value = AuthState.Authenticated
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went Wrong")
                }
            }
    }

    fun createAccount(email: String, password: String, onAccountCreated: (FirebaseUser?) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or Password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    viewModelScope.launch {
                        user?.uid?.let { userPrefs.saveUserId(it) }
                        _authState.value = AuthState.Authenticated
                    }
                    onAccountCreated(user)
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went Wrong")
                    onAccountCreated(null)
                }
            }
    }

    fun logout() {
        auth.signOut()
        viewModelScope.launch {
            userPrefs.clearUserId()
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
