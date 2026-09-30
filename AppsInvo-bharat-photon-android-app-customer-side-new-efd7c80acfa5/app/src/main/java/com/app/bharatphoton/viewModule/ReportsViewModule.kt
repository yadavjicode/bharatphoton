package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.retrofit.NetworkResult.Success
import com.app.bharatphoton.services.ReportsService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModule @Inject constructor(val reportsService: ReportsService) : ViewModel() {
    val report: MutableLiveData<NetworkResult<JsonObject>> = MutableLiveData()
    val monthlyReport: MutableLiveData<NetworkResult<JsonObject>> = MutableLiveData()

    fun monthlyReport(orderId: String, productId: String) {
        monthlyReport.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                reportsService.monthlyReport(orderId = orderId, productId = productId)
            }.onSuccess {
                if (it.isSuccessful) {
                    monthlyReport.postValue(Success(it.body()))
                } else {
                    monthlyReport.postValue(NetworkResult.Error(it.errorBody()?.string() ?: ""))
                }
            }.onFailure {
                monthlyReport.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }


    fun reports() {
        report.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                reportsService.reports()
            }.onSuccess {
                if (it.isSuccessful) {
                    report.postValue(Success(it.body()))
                } else {
                    report.postValue(NetworkResult.Error(it.errorBody()?.string() ?: ""))
                }
            }.onFailure {
                report.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }
}