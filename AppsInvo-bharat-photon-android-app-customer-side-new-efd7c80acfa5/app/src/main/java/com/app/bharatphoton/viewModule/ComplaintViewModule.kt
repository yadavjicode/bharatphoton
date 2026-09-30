package com.app.bharatphoton.viewModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.app.bharatphoton.module.complaintResponse.ComplaintHistoryResponse.ComplaintHistoryData
import com.app.bharatphoton.pagination.ComplaintLIstPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.ComplaintService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModule @Inject constructor(private val complaintService: ComplaintService) : ViewModel() {
    val registerComplaint = MutableLiveData<NetworkResult<JsonObject?>>()
    val complainDetails = MutableLiveData<NetworkResult<JsonObject?>>()
    private val _complaintListHistory = MutableLiveData<LiveData<PagingData<ComplaintHistoryData>>?>()
    val complaintListHistory: LiveData<LiveData<PagingData<ComplaintHistoryData>>> = _complaintListHistory as LiveData<LiveData<PagingData<ComplaintHistoryData>>>


    fun complaintDetails(id: String) {
        complainDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                complaintService.complaintListing(1, limit = 10, id = id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    complainDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    complainDetails.postValue(NetworkResult.Error(response.message()))
                }
            }.onFailure {
                complainDetails.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun registerComplaint(request: JsonObject) {
        registerComplaint.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                complaintService.registerComplaint(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    registerComplaint.postValue(NetworkResult.Success(response.body()))
                } else {
                    registerComplaint.postValue(NetworkResult.Error(response.message()))
                }
            }.onFailure {
                registerComplaint.postValue(NetworkResult.Error(it.message ?: ""))
            }
        }
    }

    fun complaintListHistory(id: String?) {
        if (id == null) {
            _complaintListHistory.postValue(null)
        } else {
            val pagingData = Pager(
                PagingConfig(
                    pageSize = 5, maxSize = 100, initialLoadSize = 5
                )
            ) {
                ComplaintLIstPagingSource(complaintService, id)
            }.liveData

            _complaintListHistory.postValue(pagingData)
        }
    }

}