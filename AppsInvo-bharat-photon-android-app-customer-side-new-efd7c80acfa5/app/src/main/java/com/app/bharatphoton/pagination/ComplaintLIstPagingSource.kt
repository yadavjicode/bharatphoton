package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.complaintResponse.ComplaintHistoryResponse
import com.app.bharatphoton.module.complaintResponse.ComplaintHistoryResponse.ComplaintHistoryData
import com.app.bharatphoton.services.ComplaintService
import com.google.gson.Gson


    class ComplaintLIstPagingSource(private val complaintService: ComplaintService,private val id:String) : PagingSource<Int, ComplaintHistoryData>() {
    override fun getRefreshKey(state: PagingState<Int, ComplaintHistoryData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ComplaintHistoryData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 5) 5 else params.loadSize

            val response = complaintService.complaintListing(page = pageNumber, limit = pageSize, id = id)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, ComplaintHistoryResponse::class.java)
                    val mList = listResponse.data as List<ComplaintHistoryData>

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