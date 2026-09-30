package com.app.bharatphoton.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModule : ViewModel() {
     val _profilePicUrl : MutableLiveData<String> = MutableLiveData()

    fun setProfilePicUrl(url: String) {
        "url $url".mlog()
        _profilePicUrl.value = url
    }


}