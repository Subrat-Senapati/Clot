package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipminiproject.data.model.Categories
import com.example.internshipminiproject.data.model.ProductResponse
import com.example.internshipminiproject.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel(){
    private val _categoriesList = MutableLiveData<List<Categories>>()
    val categoriesList: LiveData<List<Categories>> = _categoriesList

    fun fetchCategories(){
        viewModelScope.launch {
            try{
                val categories = RetrofitInstance.api.getCategoriesList()
                _categoriesList.value = categories
            } catch (e: Exception){
                Log.e("Error", e.message.toString())
            }
        }
    }


    private val _categoryProducts = MutableLiveData<ProductResponse>()
    val categoryProducts: LiveData<ProductResponse> = _categoryProducts

    fun fetchProductsByCategory(slug: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getProductsByCategory(slug)
                _categoryProducts.value = response
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error: ${e.message}")
            }
        }
    }
}