package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.CartSubscriptionService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartSubscriptionViewModule @Inject constructor(private val cartSubscriptionService: CartSubscriptionService) : ViewModel() {
    val addSubscriptionToCart = MutableLiveData<NetworkResult<JsonObject>>()
    val subscriptionCartList = MutableLiveData<NetworkResult<JsonObject>>()
    val updateQuantity = MutableLiveData<NetworkResult<JsonObject>>()

    fun updateQuantity(request: JsonObject) {
        updateQuantity.value = NetworkResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartSubscriptionService.updateQuantity(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    updateQuantity.postValue(NetworkResult.Success(response.body()))
                } else {
                    updateQuantity.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                updateQuantity.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun subscriptionCartList(id: String) {
        subscriptionCartList.value = NetworkResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartSubscriptionService.subscriptionCartList(id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    subscriptionCartList.postValue(NetworkResult.Success(response.body()))
                } else {
                    subscriptionCartList.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                subscriptionCartList.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun addSubscriptionToCart(request: JsonObject) {
        addSubscriptionToCart.value = NetworkResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartSubscriptionService.addSubscriptionToCart(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addSubscriptionToCart.postValue(NetworkResult.Success(response.body()))
                } else {
                    addSubscriptionToCart.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }

            }.onFailure {
                addSubscriptionToCart.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }
}