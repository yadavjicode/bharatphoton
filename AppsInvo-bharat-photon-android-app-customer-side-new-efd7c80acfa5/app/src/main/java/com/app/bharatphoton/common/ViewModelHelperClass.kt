package com.app.bharatphoton.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelHelperClass : ViewModel() {
    val sinList2LiveData = MutableLiveData<MutableSet<String>>(mutableSetOf())
}