package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import com.app.bharatphoton.pagination.ReviewLIstPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.ReviewService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModule @Inject constructor(private val reviewService: ReviewService) : ViewModel() {
    val addReview = MutableLiveData<NetworkResult<JsonObject>>()

    fun addReview(request: JsonObject) {
        addReview.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                reviewService.addReview(request)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    addReview.postValue(NetworkResult.Success(response.body()))
                } else {
                    addReview.postValue(NetworkResult.Error(response.errorBody()?.string()?:""))
                }
            }.onFailure { fail ->
                addReview.postValue(NetworkResult.Error(fail.message ?: ""))

            }
        }
    }

    fun reviewLIst(productId: String) = Pager(config = PagingConfig(
        pageSize = 5,
        maxSize = 100,
        initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        ReviewLIstPagingSource(reviewService = reviewService, productId = productId)
    }).flow
}