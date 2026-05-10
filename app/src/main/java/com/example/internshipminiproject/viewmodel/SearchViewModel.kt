package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipminiproject.data.model.ProductResponse
import com.example.internshipminiproject.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel(){
    private val _searchResults = MutableLiveData<ProductResponse?>()
    val searchResults: LiveData<ProductResponse?> = _searchResults

    fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchProducts(query)
                _searchResults.value = response
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Search error: ${e.message}")
            }
        }
    }

    fun clearResults() {
        _searchResults.value = null
    }
}