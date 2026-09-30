package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import androidx.paging.liveData
import com.app.bharatphoton.pagination.OrderListingPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.OrderService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModule @Inject constructor(private val orderService: OrderService) : ViewModel() {
    val createOrder = MutableLiveData<NetworkResult<JsonObject>>()
    val orderDetails = MutableLiveData<NetworkResult<JsonObject>>()
    val orderTrack = MutableLiveData<NetworkResult<JsonObject>>()
    val verifyPayment = MutableLiveData<NetworkResult<JsonObject>>()
    val cancelOrder = MutableLiveData<NetworkResult<JsonObject>>()
    val reOrder = MutableLiveData<NetworkResult<JsonObject>>()

    fun reOrder(request: JsonObject) {
        reOrder.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.reOrder(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    reOrder.postValue(NetworkResult.Success(response.body()))
                } else {
                    reOrder.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                reOrder.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun cancelOrder(request: JsonObject) {
        cancelOrder.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.cancelOrder(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    cancelOrder.postValue(NetworkResult.Success(response.body()))
                } else {
                    cancelOrder.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                cancelOrder.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun verifyPayment(request: JsonObject) {
        verifyPayment.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.verifyPayment(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    verifyPayment.postValue(NetworkResult.Success(response.body()))
                } else {
                    verifyPayment.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                verifyPayment.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun orderDetails(orderId: String) {
        orderDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.orderDetails(orderId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    orderDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    orderDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                orderDetails.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
    fun orderTrack(orderId: String) {
        orderTrack.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.orderTracking(orderId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    orderTrack.postValue(NetworkResult.Success(response.body()))
                } else {
                    orderTrack.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                orderTrack.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun createOrder(request: JsonObject) {
        createOrder.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                orderService.createOrder(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    createOrder.postValue(NetworkResult.Success(response.body()))
                } else {
                    createOrder.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                createOrder.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun orderListing(status: Int) = Pager(config = PagingConfig(
        pageSize = 5,
        maxSize = 100,
        initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        OrderListingPagingSource(orderService, status = status)
    }).liveData
}