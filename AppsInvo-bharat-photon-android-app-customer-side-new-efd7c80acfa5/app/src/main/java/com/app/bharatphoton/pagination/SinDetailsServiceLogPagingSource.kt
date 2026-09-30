package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.productResponse.SinDetailsServiceLogResponse
import com.app.bharatphoton.module.productResponse.SinDetailsServiceLogResponse.SinDetailsServiceLogData
import com.app.bharatphoton.services.ProductService
import com.google.gson.Gson


class SinDetailsServiceLogPagingSource(
    private val productService: ProductService, private val orderId: String, private val productId: String, private val date: String, private val sin: String
) : PagingSource<Int, SinDetailsServiceLogData>() {
    override fun getRefreshKey(state: PagingState<Int, SinDetailsServiceLogData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SinDetailsServiceLogData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = productService.sinDetailsServiceLog(page = pageNumber, limit = pageSize, date = date, orderId = orderId, productId = productId, sin = sin)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, SinDetailsServiceLogResponse::class.java)
                    val mList = listResponse.data as List<SinDetailsServiceLogData>

                    LoadResult.Page(data = mList, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mList.isEmpty()) null else pageNumber + 1)
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