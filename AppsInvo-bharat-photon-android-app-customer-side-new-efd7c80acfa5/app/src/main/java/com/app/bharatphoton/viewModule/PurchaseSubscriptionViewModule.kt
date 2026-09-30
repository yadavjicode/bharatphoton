package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import com.app.bharatphoton.pagination.PurchasedSubscriptionListingPagingSource
import com.app.bharatphoton.pagination.RenewPurchasedSubscriptionListingPagingSource
import com.app.bharatphoton.pagination.SubscriptionServiceLogPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.PurchaseSubscriptionService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseSubscriptionViewModule @Inject constructor(private val purchaseSubscriptionService: PurchaseSubscriptionService) : ViewModel() {
    val purchaseSubscription = MutableLiveData<NetworkResult<JsonObject>>()
    val verifySubscriptionPayment = MutableLiveData<NetworkResult<JsonObject>>()
    val purchaseSubscriptionDetails = MutableLiveData<NetworkResult<JsonObject>>()
    val renewPlan = MutableLiveData<NetworkResult<JsonObject>>()

    fun renewPlan(id: String,sin: String) {
        renewPlan.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                purchaseSubscriptionService.renewPlan(id, sin = sin)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    renewPlan.postValue(NetworkResult.Success(response.body()))
                } else {
                    renewPlan.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                renewPlan.postValue(NetworkResult.Error(it.message ?: ""))

            }
        }
    }

    fun purchaseSubscriptionDetails(id: String, type: Int, installationId: String) {
        purchaseSubscriptionDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                purchaseSubscriptionService.purchasedSubscriptionListing(page = 1, limit = 10, id = id, type = type, installationId = installationId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    purchaseSubscriptionDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    purchaseSubscriptionDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                purchaseSubscriptionDetails.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun verifySubscriptionPayment(request: JsonObject) {
        verifySubscriptionPayment.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                purchaseSubscriptionService.verifySubscriptionPayment(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    verifySubscriptionPayment.postValue(NetworkResult.Success(response.body()))
                } else {
                    verifySubscriptionPayment.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                verifySubscriptionPayment.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }

    }

    fun purchaseSubscription(request: JsonObject) {
        purchaseSubscription.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                purchaseSubscriptionService.purchaseSubscription(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    purchaseSubscription.postValue(NetworkResult.Success(response.body()))
                } else {
                    purchaseSubscription.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                purchaseSubscription.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }


    fun purchasedSubscriptionListing(id: String, type: Int, installationId: String) = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        PurchasedSubscriptionListingPagingSource(purchaseSubscriptionService, id = id, type = type, installationId = installationId)
    }).flow

    fun renewPurchasedSubscriptionListing(id: String, type: Int, installationId: String) = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        RenewPurchasedSubscriptionListingPagingSource(purchaseSubscriptionService, id = id, type = type, installationId = installationId)
    }).flow

    fun subscriptionServiceLog(id: String, date: String, sin: String) = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        SubscriptionServiceLogPagingSource(purchaseSubscriptionService, id = id, date = date, sin = sin)
    }).flow
}