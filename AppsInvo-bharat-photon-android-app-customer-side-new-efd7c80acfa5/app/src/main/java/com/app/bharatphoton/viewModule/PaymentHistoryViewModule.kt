package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.app.bharatphoton.module.paymentHistory.PaymentHistoryResponse
import com.app.bharatphoton.pagination.PaymentHistoryPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.PaymentHistoryService
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModule @Inject constructor(private val paymentHistoryService: PaymentHistoryService) : ViewModel() {
    val paymentHistory = MutableLiveData<NetworkResult<PaymentHistoryResponse>>()

    private var currentPage = 1
     var isLastPage = false
    private val pageSize = 10

    fun paymentHistoryRequest(loadNextPage: Boolean = false) {
        if (loadNextPage) {
            if (isLastPage) return
            currentPage++
        } else {
            currentPage = 1
            isLastPage = false
        }

        paymentHistory.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                paymentHistoryService.paymentHistory(page = currentPage, limit = pageSize)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    val responseData = response.body()
                    val paymentHistoryResponse = Gson().fromJson(responseData, PaymentHistoryResponse::class.java)
                    if (paymentHistoryResponse.data?.size!! < pageSize) {
                        isLastPage = true
                    }
                    paymentHistory.postValue(NetworkResult.Success(paymentHistoryResponse))
                } else {
                    paymentHistory.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                paymentHistory.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun paymentHistory() = Pager(PagingConfig(pageSize = 10)) {
        PaymentHistoryPagingSource(paymentHistoryService)
    }.flow.cachedIn(viewModelScope)


}