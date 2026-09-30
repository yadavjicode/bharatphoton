package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.productResponse.InstalledProductListResponse
import com.app.bharatphoton.module.productResponse.InstalledProductListResponse.InstalledProductData
import com.app.bharatphoton.services.ProductService
import com.google.gson.Gson


class InstalledProductListPagingSource(private val productService: ProductService, private val id: String, private val orderId: String) : PagingSource<Int, InstalledProductData>() {
    override fun getRefreshKey(state: PagingState<Int, InstalledProductData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, InstalledProductData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = params.loadSize

            val response = productService.installedProductList(page = pageNumber, limit = pageSize, id = id, orderId = orderId)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, InstalledProductListResponse::class.java)
                    val mList = listResponse.data as List<InstalledProductData>
                    LoadResult.Page(data = mList, prevKey = if (pageNumber > 1) pageNumber - 1 else null, nextKey = if (mList.isNotEmpty()) pageNumber + 1 else null)
                } catch (e: Exception) {
                    e.message?.mlog()
                    LoadResult.Error(e)
                }
            } else {
                LoadResult.Error(Exception("Error"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}