package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.PayNowService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayNowViewModule @Inject constructor(private val payNowService: PayNowService) : ViewModel() {
    val payNow = MutableLiveData<NetworkResult<JsonObject>>()
    val verifyOnlinePayment = MutableLiveData<NetworkResult<JsonObject>>()

    fun verifyOnlinePayment(request: JsonObject) {
        verifyOnlinePayment.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                payNowService.verifyOnlinePayment(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    verifyOnlinePayment.postValue(NetworkResult.Success(it.body()))
                } else {
                    verifyOnlinePayment.postValue(it.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                verifyOnlinePayment.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun payNowRequest(request: JsonObject) {
        payNow.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                payNowService.payNow(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    payNow.postValue(NetworkResult.Success(response.body()))
                } else {
                    payNow.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                payNow.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }
}