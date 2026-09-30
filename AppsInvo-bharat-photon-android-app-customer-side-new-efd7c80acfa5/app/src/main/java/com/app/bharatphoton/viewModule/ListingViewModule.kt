package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.ListingService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingViewModule @Inject constructor(private val listingService: ListingService) : ViewModel() {
    val categoryList = MutableLiveData<NetworkResult<JsonObject>>()
    val subCategoryList = MutableLiveData<NetworkResult<JsonObject>>()
    val stateList = MutableLiveData<NetworkResult<JsonObject>>()
    val cityList = MutableLiveData<NetworkResult<JsonObject>>()
    val productList = MutableLiveData<NetworkResult<JsonObject>>()

    fun productList(categoryId: String) {
        productList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                listingService.productListing(categoryId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    productList.postValue(NetworkResult.Success(response.body()))
                } else {
                    productList.postValue(response.errorBody()?.let { NetworkResult.Error(it.string()) })
                }
            }.onFailure {
                productList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun cityListing(stateName: String) {
        cityList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                listingService.cityListing(stateName = stateName)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    cityList.postValue(NetworkResult.Success(response.body()))
                } else {
                    cityList.postValue(response.errorBody()?.let { NetworkResult.Error(it.string()) })
                }
            }.onFailure {
                cityList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun stateListing() {
        stateList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                listingService.stateListing()
            }.onSuccess {
                if (it.isSuccessful) {
                    stateList.postValue(NetworkResult.Success(it.body()))
                } else {
                    stateList.postValue(it.errorBody()?.let { it1 -> NetworkResult.Error(it1.string()) })
                }
            }.onFailure {
                stateList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun subCategory(categoryId: String) {
        subCategoryList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                listingService.subCategoryListing(categoryId)
            }.onSuccess {
                if (it.isSuccessful) {
                    subCategoryList.postValue(NetworkResult.Success(it.body()))
                } else {
                    subCategoryList.postValue(NetworkResult.Error(it.errorBody().toString()))
                }
            }.onFailure {
                subCategoryList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun categoryListing() {
        categoryList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                listingService.categoryListing()
            }.onSuccess {
                if (it.isSuccessful) {
                    categoryList.postValue(NetworkResult.Success(it.body()))
                } else {
                    categoryList.postValue(it.errorBody()?.let { it1 -> NetworkResult.Error(it1.string()) })
                }
            }.onFailure {
                categoryList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }
}