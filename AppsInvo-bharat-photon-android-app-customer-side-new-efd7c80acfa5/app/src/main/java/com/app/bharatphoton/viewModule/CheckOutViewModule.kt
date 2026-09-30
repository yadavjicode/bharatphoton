package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.CheckOutService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckOutViewModule @Inject constructor(private val checkOutService: CheckOutService) : ViewModel() {
    val checkOut = MutableLiveData<NetworkResult<JsonObject>>()
    val checkInstallation = MutableLiveData<NetworkResult<JsonObject>>()
    val removeInstallation = MutableLiveData<NetworkResult<JsonObject>>()

    fun removeInstallation(request: JsonObject) {
        removeInstallation.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                checkOutService.removeInstallation(request = request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    removeInstallation.postValue(NetworkResult.Success(response.body()))
                } else {
                    removeInstallation.postValue(response.errorBody()?.let { NetworkResult.Error(it.string()) })
                }
            }.onFailure {
                removeInstallation.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun checkInstallation(request: JsonObject) {
        checkInstallation.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                checkOutService.checkInstallation(request = request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    checkInstallation.postValue(NetworkResult.Success(response.body()))
                } else {
                    checkInstallation.postValue(response.errorBody()?.let { NetworkResult.Error(it.string()) })
                }
            }.onFailure {
                checkInstallation.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun checkout(cartId: String) {
        checkOut.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                checkOutService.checkOut(cartId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    checkOut.postValue(NetworkResult.Success(response.body()))
                } else {
                    checkOut.postValue(response.errorBody()?.let { NetworkResult.Error(it.string()) })
                }
            }.onFailure {
                checkOut.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}