package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.retrofit.NetworkResult.Error
import com.app.bharatphoton.retrofit.NetworkResult.Success
import com.app.bharatphoton.services.AuthService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModule @Inject constructor(private val apiService: AuthService) : ViewModel() {

    val changePassword = MutableLiveData<NetworkResult<JsonObject>>()
    val cmsData = MutableLiveData<NetworkResult<JsonObject>>()
    val deleteAccount = MutableLiveData<NetworkResult<JsonObject>>()
    val forgetPassword = MutableLiveData<NetworkResult<JsonObject>>()
    val login = MutableLiveData<NetworkResult<JsonObject?>>()
    val register = MutableLiveData<NetworkResult<JsonObject?>>()
    val resendOtp = MutableLiveData<NetworkResult<JsonObject>>()
    val resetPassword = MutableLiveData<NetworkResult<JsonObject>>()
    val s3 = MutableLiveData<NetworkResult<JsonObject>>()
    val socialLogin = MutableLiveData<NetworkResult<JsonObject>>()
    val troubleShootVideos = MutableLiveData<NetworkResult<JsonObject>>()
    val updateDeviceToken = MutableLiveData<NetworkResult<JsonObject>>()
    val verifyOtp = MutableLiveData<NetworkResult<JsonObject>>()
    val faqList = MutableLiveData<NetworkResult<JsonObject>>()
    val addQuery = MutableLiveData<NetworkResult<JsonObject>>()

    fun addQuery(request: JsonObject) {
        addQuery.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.addQuery(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addQuery.postValue(Success(response.body()))
                } else {
                    addQuery.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                addQuery.postValue(Error(it.message.toString()))
            }
        }
    }

    fun faqList() {
        faqList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.faqList()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    faqList.postValue(Success(response.body()))
                } else {
                    faqList.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                faqList.postValue(Error(it.message.toString()))
            }
        }
    }

    fun changePassword(request: JsonObject) {
        changePassword.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.changePassword(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    changePassword.postValue(Success(it.body()))
                } else {
                    changePassword.postValue(it.errorBody()?.string()?.let { it1 -> Error(it1) })
                }
            }.onFailure {
                changePassword.postValue(Error(it.message.toString()))
            }
        }
    }

    fun cmsData(userType: Int, type: String) {
        cmsData.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.cmsData(userType = userType, type = type)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    cmsData.postValue(Success(response.body()))
                } else {
                    cmsData.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                cmsData.postValue(Error(it.message.toString()))
            }
        }
    }

    fun deleteAccount(request: JsonObject) {
        deleteAccount.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.deleteAccount(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    deleteAccount.postValue(Success(response.body()))
                } else {
                    deleteAccount.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                deleteAccount.postValue(Error(it.message.toString()))
            }
        }
    }

    fun forgetPassword(request: JsonObject) {
        forgetPassword.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.forgetPassword(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    forgetPassword.postValue(Success(it.body()))
                } else {
                    forgetPassword.postValue(it.errorBody()?.string()?.let { it1 -> Error(it1) })
                }
            }.onFailure {
                forgetPassword.postValue(Error(it.message.toString()))
            }
        }
    }

    fun login(request: JsonObject) {
        login.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.login(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    login.postValue(Success(it.body()))
                } else {
                    login.postValue(it.errorBody()?.string()?.let { it1 -> Error(it1) })
                }
            }.onFailure {
                login.postValue(Error(it.message.toString()))
            }
        }
    }

    fun register(request: JsonObject) {
        register.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.register(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    register.postValue(Success(response.body()))
                } else {
                    register.postValue(response.errorBody()?.string()?.let { Error(it) })
                }

            }.onFailure {
                register.postValue(Error(it.message.toString()))
            }
        }
    }

    fun resendOtp(request: JsonObject) {
        resendOtp.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.resendOtp(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    resendOtp.postValue(Success(it.body()))
                } else {
                    resendOtp.postValue(it.errorBody()?.string()?.let { it1 -> Error(it1) })
                }
            }.onFailure {
                resendOtp.postValue(Error(it.message.toString()))
            }
        }
    }

    fun resetPassword(request: JsonObject) {
        resetPassword.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.resetPassword(request)
            }.onSuccess {
                if (it.isSuccessful) {
                    resetPassword.postValue(Success(it.body()))
                } else {
                    resetPassword.postValue(it.errorBody()?.string()?.let { it1 -> Error(it1) })
                }
            }.onFailure {
                resetPassword.postValue(Error(it.message.toString()))
            }
        }
    }

    fun s3() {
        s3.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.s3()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    s3.postValue(Success(response.body()))
                } else {
                    s3.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                s3.postValue(Error(it.message.toString()))
            }
        }
    }

    fun socialLogin(request: JsonObject) {
        socialLogin.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.socialLogin(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    socialLogin.postValue(Success(response.body()))
                } else {
                    socialLogin.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                socialLogin.postValue(Error(it.message.toString()))
            }
        }
    }

    fun troubleShootVideo(categoryId: String, productId: String) {
        troubleShootVideos.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.troubleShootingVideo(categoryId = categoryId, productId = productId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    troubleShootVideos.postValue(Success(response.body()))
                } else {
                    troubleShootVideos.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                troubleShootVideos.postValue(Error(it.message.toString()))
            }
        }
    }

    fun updateDeviceToken(request: JsonObject) {
        updateDeviceToken.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.updateDeviceToken(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    updateDeviceToken.postValue(Success(response.body()))
                } else {
                    updateDeviceToken.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                updateDeviceToken.postValue(Error(it.message.toString()))
            }
        }
    }

    fun verifyOtp(token: String, request: JsonObject) {
        verifyOtp.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.verifyOtp(token, request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    verifyOtp.postValue(Success(response.body()))
                } else {
                    verifyOtp.postValue(response.errorBody()?.string()?.let { Error(it) })
                }
            }.onFailure {
                verifyOtp.postValue(Error(it.message.toString()))
            }
        }
    }

}