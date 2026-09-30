package com.app.bharatphoton.viewModule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSourceFactory
import androidx.paging.cachedIn
import com.app.bharatphoton.pagination.InstalledProductListPagingSource
import com.app.bharatphoton.pagination.ProductListPagingSource
import com.app.bharatphoton.pagination.SinDetailsServiceLogPagingSource
import com.app.bharatphoton.pagination.SubCatProductListPagingSource
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.services.ProductService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModule @Inject constructor(private val productService: ProductService) : ViewModel() {
    val productDetails = MutableLiveData<NetworkResult<JsonObject?>>()
    val productList = MutableLiveData<NetworkResult<JsonObject?>>()
    val installedProductDetails = MutableLiveData<NetworkResult<JsonObject?>>()
    val sinDetails = MutableLiveData<NetworkResult<JsonObject?>>()

    fun sinDetails(orderId: String, productId: String, installationId: String) {
        sinDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                productService.sinDetails(orderId = orderId, productId = productId, installationId = installationId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    sinDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    sinDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                sinDetails.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun installedProductDetails(id: String, orderId: String) {
        installedProductDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                productService.installedProductList(page = 1, limit = 10, id = id, orderId = orderId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    installedProductDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    installedProductDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                installedProductDetails.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun productList(categoryId: String, search: String, deviceId: String) {
        productList.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                productService.productList(categoryId = categoryId, search = search, deviceId = deviceId, page = 1, limit = 100, subcategoryId = "")
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    productList.postValue(NetworkResult.Success(response.body()))
                } else {
                    productList.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                productList.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }


    fun getProductDetails(id: String, deviceId: String) {
        productDetails.postValue(NetworkResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                productService.getProductDetails(id, deviceId = deviceId)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    productDetails.postValue(NetworkResult.Success(response.body()))
                } else {
                    productDetails.postValue(response.errorBody()?.string()?.let { NetworkResult.Error(it) })
                }
            }.onFailure {
                productDetails.postValue(NetworkResult.Error(it.message.toString()))
            }
        }
    }

    fun installedProductList(id: String, orderId: String) = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        InstalledProductListPagingSource(productService, id = id, orderId = orderId)
    }).flow.cachedIn(viewModelScope)


    fun productListForCategoryData(categoryId: String, deviceId: String, subcategoryId: String, search: String) = Pager(config = PagingConfig(
        pageSize = 10,
    ), pagingSourceFactory = PagingSourceFactory {
        ProductListPagingSource(productService, categoryId = categoryId, deviceId = deviceId, subcategoryId = subcategoryId, search = search)
    }).flow

    fun subCategoryProductList(categoryId: String, deviceId: String, subcategoryId: String) = Pager(config = PagingConfig(
        pageSize = 10,
    ), pagingSourceFactory = PagingSourceFactory {
        SubCatProductListPagingSource(productService, categoryId = categoryId, deviceId = deviceId, subcategoryId = subcategoryId)
    }).flow.cachedIn(viewModelScope)

    fun sinDetailsServiceLog(orderId: String, date: String, productId: String, sin: String) = Pager(config = PagingConfig(
        pageSize = 5, maxSize = 100, initialLoadSize = 5
    ), pagingSourceFactory = PagingSourceFactory {
        SinDetailsServiceLogPagingSource(productService, orderId = orderId, productId = productId, date = date, sin = sin)
    }).flow
}