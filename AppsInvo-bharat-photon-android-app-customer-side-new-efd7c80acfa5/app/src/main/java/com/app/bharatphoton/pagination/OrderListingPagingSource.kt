package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.orderResponse.OrderListingResponse
import com.app.bharatphoton.module.orderResponse.OrderListingResponse.OrderListingData
import com.app.bharatphoton.services.OrderService
import com.google.gson.Gson


class OrderListingPagingSource(private val wishListService: OrderService, private val status: Int) : PagingSource<Int, OrderListingData>() {
    override fun getRefreshKey(state: PagingState<Int, OrderListingData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderListingData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = wishListService.orderListing(page = pageNumber, limit = pageSize, status = status)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, OrderListingResponse::class.java)
                    val mList = listResponse.data as List<OrderListingData>
                    SharedData.orderId = mList.map {
                        it.uuid ?: ""
                    }

                    LoadResult.Page(data = mList, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mList.isEmpty()) null else pageNumber + 1)
                } catch (e: Exception) {
                    e.message.mlog()
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