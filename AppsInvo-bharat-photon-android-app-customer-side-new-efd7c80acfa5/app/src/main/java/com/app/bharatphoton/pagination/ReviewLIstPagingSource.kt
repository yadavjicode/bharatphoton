package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.reviewResponse.ReviewListResponse
import com.app.bharatphoton.module.reviewResponse.ReviewListResponse.ReviewListData
import com.app.bharatphoton.services.ReviewService
import com.google.gson.Gson


class ReviewLIstPagingSource(private val reviewService: ReviewService, private val productId: String) : PagingSource<Int, ReviewListData>() {
    override fun getRefreshKey(state: PagingState<Int, ReviewListData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReviewListData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = reviewService.getReviews(page = pageNumber, limit = pageSize, productId = productId)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, ReviewListResponse::class.java)
                    val mList = listResponse.data as List<ReviewListData>

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