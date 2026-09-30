package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.CartService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModule @Inject constructor(private val cartService: CartService) : ViewModel() {


    val addToCart = MutableLiveData<NetworkResult<JsonObject>>()
    val cartDetails = MutableLiveData<NetworkResult<JsonObject>>()
    val cartItemCount = MutableLiveData<NetworkResult<JsonObject>>()
    val removeFromCart = MutableLiveData<NetworkResult<JsonObject>>()

    fun addToCart(request: JsonObject) {
        addToCart.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartService.addToCart(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addToCart.postValue(NetworkResult.Success(response.body()))
                } else {
                    addToCart.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                addToCart.postValue(NetworkResult.Error(it.message.toString()))

            }
        }
    }

    fun cartDetails(deviceId: String) {
        cartDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartService.cartDetails(deviceId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    cartDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    cartDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                cartDetails.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun cartItemCount(deviceId: String) {
        cartItemCount.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartService.cartItemCount(deviceId)
            }.onSuccess { resposne ->
                if (resposne.isSuccessful) {
                    cartItemCount.postValue(NetworkResult.Success(resposne.body()))
                } else {
                    cartItemCount.postValue(resposne.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                cartItemCount.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun removeFromCart(request: JsonObject) {
        removeFromCart.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                cartService.removeFromCart(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    removeFromCart.postValue(NetworkResult.Success(response.body()))
                } else {
                    removeFromCart.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                removeFromCart.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}