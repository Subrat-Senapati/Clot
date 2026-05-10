package com.example.internshipminiproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.internshipminiproject.data.model.Product
import com.example.internshipminiproject.data.model.ProductDetails
import com.example.internshipminiproject.data.model.ProductResponse
import com.example.internshipminiproject.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val _productResponseList = MutableLiveData<ProductResponse>()
    val productResponseList: LiveData<ProductResponse> = _productResponseList
    private var isProductFetched = false
    fun fetchProducts() {
        if (isProductFetched) return
        viewModelScope.launch {
            try {
                val productResponse = RetrofitInstance.api.getProductList()
                val shuffledProducts = productResponse.products.shuffled()
                val updatedResponse = productResponse.copy(products = shuffledProducts)
                _productResponseList.value = updatedResponse
                isProductFetched = true
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products: ${e.message}")
            }
        }
    }


    private val _singleProduct = MutableLiveData<ProductDetails>()
    val singleProduct: LiveData<ProductDetails> = _singleProduct

    fun fetchProductById(id: Int) {
        viewModelScope.launch {
            try {
                val product = RetrofitInstance.api.getProductById(id)
                _singleProduct.value = product
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error: ${e.message}")
            }
        }
    }

    suspend fun fetchProductsByIds(ids: List<Int>): List<Product> {
        val products = mutableListOf<Product>()

        for (id in ids) {
            try {
                val productDetails = RetrofitInstance.api.getProductById(id)
                products.add(
                    Product(
                        id = productDetails.id,
                        title = productDetails.title,
                        price = productDetails.price,
                        category = productDetails.category,
                        thumbnail = productDetails.thumbnail
                    )
                )
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Failed to fetch product $id: ${e.message}")
            }
        }

        return products
    }

}


