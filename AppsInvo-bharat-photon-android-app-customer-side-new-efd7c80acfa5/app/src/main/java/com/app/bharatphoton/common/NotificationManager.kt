package com.app.bharatphoton.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NotificationManage {
    private val _notificationCount = MutableLiveData(0)
    val notificationCount: LiveData<Int> get() = _notificationCount

    fun increaseNotificationCount() {
        _notificationCount.postValue((_notificationCount.value ?: 0) + 1)
    }
}