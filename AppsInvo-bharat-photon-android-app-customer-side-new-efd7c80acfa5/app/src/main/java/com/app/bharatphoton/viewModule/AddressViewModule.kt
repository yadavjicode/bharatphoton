package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.AddressService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModule @Inject constructor(private val addressService: AddressService) : ViewModel() {
    val addressList = MutableLiveData<NetworkResult<JsonObject>>()
    val addAddress = MutableLiveData<NetworkResult<JsonObject>>()
    val deleteAddress = MutableLiveData<NetworkResult<JsonObject>>()
    val updateDeliveryAddress = MutableLiveData<NetworkResult<JsonObject>>()
    val addInstallationAddress = MutableLiveData<NetworkResult<JsonObject>>()

    fun addInstallationAddress(request: JsonObject) {
        addInstallationAddress.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                addressService.addInstallationAddress(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addInstallationAddress.postValue(NetworkResult.Success(response.body()))
                } else {
                    addInstallationAddress.postValue(NetworkResult.Error(response.errorBody().toString()))
                }
            }.onFailure {
                addInstallationAddress.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun updateDeliveryAddress(request: JsonObject) {
        updateDeliveryAddress.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                addressService.updateDeliveryAddress(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    updateDeliveryAddress.postValue(NetworkResult.Success(response.body()))
                } else {
                    updateDeliveryAddress.postValue(NetworkResult.Error(response.errorBody().toString()))
                }
            }.onFailure {
                updateDeliveryAddress.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun deleteAddress(id: String) {
        deleteAddress.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                addressService.deleteAddress(id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    deleteAddress.postValue(NetworkResult.Success(response.body()))
                } else {
                    deleteAddress.postValue(NetworkResult.Error(response.errorBody().toString()))
                }
            }
        }
    }

    fun addAddress(request: JsonObject) {
        addAddress.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                addressService.addAddress(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addAddress.postValue(NetworkResult.Success(response.body()))
                } else {
                    addAddress.postValue(NetworkResult.Error(response.message()))
                }
            }.onFailure {
                addAddress.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun addressList(id: String) {
        addressList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                addressService.addressList(id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addressList.postValue(NetworkResult.Success(response.body()))
                } else {
                    addressList.postValue(NetworkResult.Error(response.errorBody().toString()))
                }
            }.onFailure {
                addressList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}