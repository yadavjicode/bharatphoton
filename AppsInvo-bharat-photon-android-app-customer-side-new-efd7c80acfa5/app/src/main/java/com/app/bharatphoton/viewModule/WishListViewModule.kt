package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import androidx.paging.liveData
import com.app.bharatphoton.pagination.WishListPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.WishListService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModule @Inject constructor(private val wishListService: WishListService) : ViewModel() {
    val addToWishList = MutableLiveData<NetworkResult<JsonObject>>()
    val deleteWishList = MutableLiveData<NetworkResult<JsonObject>>()

    fun deleteWishList(id: String) {
        deleteWishList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                wishListService.deleteWishList(id)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    deleteWishList.postValue(NetworkResult.Success(response.body()))
                } else {
                    deleteWishList.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                deleteWishList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun addToWishList(request: JsonObject) {
        addToWishList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                wishListService.addToWishList(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addToWishList.postValue(NetworkResult.Success(response.body()))
                } else {
                    addToWishList.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                addToWishList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun wishList() = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        WishListPagingSource(wishListService)
    }).liveData
}