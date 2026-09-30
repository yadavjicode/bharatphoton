package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.ProfileService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModule @Inject constructor(private val profileService: ProfileService) : ViewModel() {

    val changeEmailPhone = MutableLiveData<NetworkResult<JsonObject>>()
    val personalEditProfile = MutableLiveData<NetworkResult<JsonObject>>()
    val userProfile = MutableLiveData<NetworkResult<JsonObject>>()
    val verifyEmailPhone = MutableLiveData<NetworkResult<JsonObject>>()

    fun changeEmailPhone(request: JsonObject) {
        changeEmailPhone.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                profileService.changeEmailPhone(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    changeEmailPhone.postValue(NetworkResult.Success(it.body()))
                } else {
                    changeEmailPhone.postValue(it.errorBody()?.string()?.let { it1 -> NetworkResult.Error(it1) })
                }
            }.onFailure {
                changeEmailPhone.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun personalEditProfile(request: JsonObject) {
        personalEditProfile.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                profileService.editUser(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    personalEditProfile.postValue(NetworkResult.Success(response.body()))
                } else {
                    personalEditProfile.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                personalEditProfile.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun userProfile() {
        userProfile.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                profileService.getUserProfile()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    userProfile.postValue(NetworkResult.Success(response.body()))
                } else {
                    userProfile.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                userProfile.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun verifyEmailPhone(request: JsonObject) {
        verifyEmailPhone.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                profileService.verifyEmailPhone(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    verifyEmailPhone.postValue(NetworkResult.Success(it.body()))
                } else {
                    verifyEmailPhone.postValue(it.errorBody()?.string()?.let { it1 -> NetworkResult.Error(it1) })
                }
            }.onFailure {
                verifyEmailPhone.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

}