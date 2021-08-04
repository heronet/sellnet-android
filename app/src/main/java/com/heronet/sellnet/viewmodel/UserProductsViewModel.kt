package com.heronet.sellnet.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heronet.sellnet.data.ProductsRepository
import com.heronet.sellnet.dataStore
import com.heronet.sellnet.model.AuthData
import com.heronet.sellnet.model.Product
import com.heronet.sellnet.util.Constants
import com.heronet.sellnet.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    @ApplicationContext private val context: Context
) :
    ViewModel() {
    var products = mutableStateOf<List<Product>>(listOf())
    var productsCount = mutableStateOf(0)
    var isLoading = mutableStateOf(false)
    var isDeleting = mutableStateOf(false)
    var isDeleteSuccessful = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    private var authData: AuthData? = null

    private var currentPage = 1
    init {
        getProducts()
    }
    fun getProducts() {
        viewModelScope.launch {
            isLoading.value = true
            val dataStore = context.dataStore.data.first()

             authData = AuthData(
                id = dataStore[Constants.ID]!!,
                name = dataStore[Constants.NAME]!!,
                token = dataStore[Constants.TOKEN]!!,
                roles = listOf(dataStore[Constants.ROLE]!!)
            )

            when (val response = repository.getProducts(
                currentPage,
                Constants.PAGE_SIZE,
                sellerId = authData!!.id
            )) {
                is Resource.Error -> {
                    errorMessage.value = response.message!!
                    isLoading.value = false
                }
                is Resource.Success -> {
                    productsCount.value = response.data!!.size
                    errorMessage.value = ""
                    products.value += response.data.data
                    ++currentPage
                    isLoading.value = false
                }
            }
        }
    }
    fun deleteProduct(id: String) {
        viewModelScope.launch {
            isDeleting.value = true
            when(val response =  repository.deleteProduct(id, "Bearer ${authData!!.token}")) {
                is Resource.Success -> {
                    isDeleting.value = false
                    isDeleteSuccessful.value = true
                    errorMessage.value = ""
                }
                is Resource.Error -> {
                    errorMessage.value = response.message!!
                    isDeleting.value  = false
                    isDeleteSuccessful.value = false
                }
            }
        }
    }
    fun resetDeleteStatus() {
        isDeleteSuccessful.value = false
    }
}