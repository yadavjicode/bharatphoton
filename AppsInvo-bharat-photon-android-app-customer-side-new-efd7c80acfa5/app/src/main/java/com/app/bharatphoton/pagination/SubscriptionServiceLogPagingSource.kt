package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.purchaseSubscriptionResponse.SubscriptionServiceLog
import com.app.bharatphoton.module.purchaseSubscriptionResponse.SubscriptionServiceLog.SubscriptionServiceData
import com.app.bharatphoton.services.PurchaseSubscriptionService
import com.google.gson.Gson


class SubscriptionServiceLogPagingSource(private val purchaseSubscriptionService: PurchaseSubscriptionService, private val id: String, private val date: String, private val sin: String) :
    PagingSource<Int, SubscriptionServiceData>() {
    override fun getRefreshKey(state: PagingState<Int, SubscriptionServiceData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SubscriptionServiceData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = purchaseSubscriptionService.subscriptionServiceLog(page = pageNumber, limit = pageSize, id = id, date = date, sin = sin)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, SubscriptionServiceLog::class.java)
                    val mList = listResponse.data as List<SubscriptionServiceData>

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