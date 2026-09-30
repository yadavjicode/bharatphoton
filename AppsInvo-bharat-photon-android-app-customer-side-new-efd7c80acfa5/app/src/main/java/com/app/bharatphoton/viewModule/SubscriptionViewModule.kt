package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.SubscriptionService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModule @Inject constructor(private val subscriptionService: SubscriptionService) : ViewModel() {
    val subscriptionListing = MutableLiveData<NetworkResult<JsonObject>>()
    val checkSubscription = MutableLiveData<NetworkResult<JsonObject>>()

    fun checkSubscription(request: JsonObject) {
        checkSubscription.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                subscriptionService.checkSubscription(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    checkSubscription.postValue(NetworkResult.Success(response.body()))
                } else {
                    checkSubscription.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                checkSubscription.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun subscriptionListing(productId: String, pincode: String) {
        subscriptionListing.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                subscriptionService.subscriptionListing(productId = productId, pincode = pincode)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    subscriptionListing.postValue(NetworkResult.Success(response.body()))
                } else {
                    subscriptionListing.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                subscriptionListing.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}