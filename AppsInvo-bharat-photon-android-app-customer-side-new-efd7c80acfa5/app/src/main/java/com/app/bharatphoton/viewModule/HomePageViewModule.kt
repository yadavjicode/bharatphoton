package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.HomePageService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModule @Inject constructor(private val homePageService: HomePageService?) : ViewModel() {
    val homePage = MutableLiveData<NetworkResult<JsonObject>>()

    fun homePage() {
        homePage.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                homePageService?.homePage()
            }.onSuccess { response ->
                if (response?.isSuccessful == true) {
                    homePage.postValue(NetworkResult.Success(response.body()))
                } else {
                    homePage.postValue(response?.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                homePage.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}