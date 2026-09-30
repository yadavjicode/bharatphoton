package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.paymentHistory.PaymentHistoryResponse
import com.app.bharatphoton.module.paymentHistory.PaymentHistoryResponse.PaymentHistoryData
import com.app.bharatphoton.services.PaymentHistoryService
import com.google.gson.Gson


class PaymentHistoryPagingSource(private val paymentHistoryService: PaymentHistoryService) : PagingSource<Int, PaymentHistoryData>() {
    override fun getRefreshKey(state: PagingState<Int, PaymentHistoryData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaymentHistoryData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = paymentHistoryService.paymentHistory(page = pageNumber, limit = pageSize)

            if (response.isSuccessful) {
                try {
                    "in try called".mlog()
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, PaymentHistoryResponse::class.java)
                    val mList = listResponse.data as List<PaymentHistoryData>

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