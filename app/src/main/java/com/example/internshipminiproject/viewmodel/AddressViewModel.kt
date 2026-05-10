package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internshipminiproject.data.model.Address
import com.example.internshipminiproject.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import java.util.UUID

@HiltViewModel
class AddressViewModel @Inject constructor() : ViewModel() {

    private val _addresses = MutableLiveData<List<Address>>()
    val addresses: LiveData<List<Address>> = _addresses

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // 🔹 Fetch addresses from User document field
    fun fetchUserAddresses() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _addresses.value = user?.addresses ?: emptyList()
            }
            .addOnFailureListener {
                _addresses.value = emptyList()
            }
    }

    // 🔹 Set selected address as default (only one at a time)
    fun setDefaultAddress(selectedAddressId: String) {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            val updatedAddresses = user?.addresses?.map {
                it.copy(default = it.addressId == selectedAddressId)
            } ?: return@addOnSuccessListener

            userDoc.set(mapOf("addresses" to updatedAddresses), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("Address", "Default address updated successfully")
                    _addresses.value = updatedAddresses
                }
                .addOnFailureListener {
                    Log.e("Address", "Failed to update address: ${it.message}")
                }
        }.addOnFailureListener {
            Log.e("Address", "Failed to fetch user document: ${it.message}")
        }
    }


    fun addAddress(
        address: Address,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            val currentAddresses = user?.addresses?.toMutableList() ?: mutableListOf()

            val addressWithId = address.copy(addressId = UUID.randomUUID().toString())

            // If this is the first address, set as default
            val updatedAddress = if (currentAddresses.isEmpty()) {
                addressWithId.copy(default = true)
            } else {
                addressWithId
            }

            currentAddresses.add(updatedAddress)

            userDoc.update("addresses", currentAddresses)
                .addOnSuccessListener {
                    _addresses.value = currentAddresses
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun updateAddress(
        updatedAddress: Address,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        val userDoc = firestore.collection("users").document(userId)

        userDoc.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            val currentAddresses = user?.addresses?.toMutableList() ?: return@addOnSuccessListener

            val updatedList = currentAddresses.map {
                if (it.addressId == updatedAddress.addressId) updatedAddress else it
            }

            userDoc.update("addresses", updatedList)
                .addOnSuccessListener {
                    _addresses.value = updatedList
                    onSuccess()
                }
                .addOnFailureListener { onFailure(it) }
        }.addOnFailureListener { onFailure(it) }
    }



    private val _defaultAddress = MutableLiveData<Address?>()
    val defaultAddress: LiveData<Address?> = _defaultAddress

    fun fetchDefaultAddress() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _defaultAddress.value = user?.addresses?.find { it.default }
            }
            .addOnFailureListener {
                _defaultAddress.value = null
            }
    }

}

