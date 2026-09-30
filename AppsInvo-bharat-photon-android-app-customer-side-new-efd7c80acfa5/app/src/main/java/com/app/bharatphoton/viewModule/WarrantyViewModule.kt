package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.app.bharatphoton.pagination.WarrantyLIstPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.WarrantyService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarrantyViewModule @Inject constructor(private val warrantyService: WarrantyService) : ViewModel() {
    val availableWarranty = MutableLiveData<NetworkResult<JsonObject>>()
    val renewWarranty = MutableLiveData<NetworkResult<JsonObject>>()
    val verifyWarrantyPayment = MutableLiveData<NetworkResult<JsonObject>>()

    fun verifyWarrantyPayment(request: JsonObject){
        verifyWarrantyPayment.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                warrantyService.verifyWarrantyPayment(request)
            }.onSuccess { response->
                if (response.isSuccessful){
                    verifyWarrantyPayment.postValue(NetworkResult.Success(response.body()))
                }else{
                    verifyWarrantyPayment.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                verifyWarrantyPayment.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun renewWarranty(request: JsonObject) {
        renewWarranty.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                warrantyService.renewWarranty(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    renewWarranty.postValue(NetworkResult.Success(response.body()))
                } else {
                    renewWarranty.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                renewWarranty.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun availableWarranty(productId: String) {
        availableWarranty.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                warrantyService.availableWarranty(productId)
            }.onSuccess {
                if (it.isSuccessful) {
                    availableWarranty.postValue(NetworkResult.Success(it.body()))
                } else {
                    availableWarranty.postValue(it.errorBody()?.string()?.let { it1 -> NetworkResult.Error(it1) })
                }
            }.onFailure {
                availableWarranty.postValue(NetworkResult.Error(it.message ?: ""))
            }

        }
    }

    fun warrantyList(type:Int) = Pager(PagingConfig(pageSize = 5,
        maxSize = 100,
        initialLoadSize = 5)) {
        WarrantyLIstPagingSource(warrantyService, type = type)
    }.liveData

}