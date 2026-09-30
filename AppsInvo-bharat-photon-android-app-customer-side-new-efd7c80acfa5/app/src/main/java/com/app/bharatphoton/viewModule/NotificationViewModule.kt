package com.app.bharatphoton.viewModule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import com.app.bharatphoton.pagination.NotificationLIstPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.NotificationService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModule @Inject constructor(private val notificationService: NotificationService) : ViewModel() {
    val notificationCount = MutableLiveData<NetworkResult<JsonObject>>()
    private val _notificationCount = MutableLiveData(0)
    val notificationLiveCount: LiveData<Int> get() = _notificationCount

    fun increaseNotificationCount() {
        _notificationCount.value = (_notificationCount.value ?: 0) + 1
    }

    fun notificationCount() {
        notificationCount.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                notificationService.notificationCount()
            }.onSuccess {
                if (it.isSuccessful) {
                    notificationCount.postValue(NetworkResult.Success(it.body()))
                } else {
                    notificationCount.postValue(NetworkResult.Error(it.message()))
                }
            }.onFailure {
                notificationCount.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun notificationList(read: String) = Pager(config = PagingConfig(
        pageSize = 10, maxSize = 100, initialLoadSize = 10
    ), pagingSourceFactory = PagingSourceFactory {
        NotificationLIstPagingSource(notificationService = notificationService, read = read)
    }).flow
}