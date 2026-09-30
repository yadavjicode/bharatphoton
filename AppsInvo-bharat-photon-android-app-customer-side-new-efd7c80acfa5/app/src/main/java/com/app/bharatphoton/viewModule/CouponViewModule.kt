package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.CouponService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CouponViewModule @Inject constructor(private val couponService: CouponService) : ViewModel() {

    val applyCoupon = MutableLiveData<NetworkResult<JsonObject>>()
    val couponList = MutableLiveData<NetworkResult<JsonObject>>()
    val removeCoupon = MutableLiveData<NetworkResult<JsonObject>>()

    fun applyCoupon(request: JsonObject) {
        applyCoupon.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                couponService.applyCoupon(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    applyCoupon.postValue(NetworkResult.Success(response.body()))
                } else {
                    applyCoupon.postValue(NetworkResult.Error(response.errorBody()?.string() ?: ""))
                }
            }.onFailure {
                applyCoupon.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun couponList(cartId: String) {
        couponList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                couponService.getCouponList(cartId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    couponList.postValue(NetworkResult.Success(response.body()))
                } else {
                    couponList.postValue(NetworkResult.Error(response.errorBody()?.string() ?: ""))
                }
            }.onFailure {
                couponList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun removeCoupon(request: JsonObject) {
        removeCoupon.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                couponService.removeCoupon(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    removeCoupon.postValue(NetworkResult.Success(response.body()))
                } else {
                    removeCoupon.postValue(NetworkResult.Error(response.errorBody()?.string() ?: ""))
                }
            }
        }
    }
}