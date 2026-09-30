package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.warrantyApiResponse.WarrantyListResponse
import com.app.bharatphoton.module.warrantyApiResponse.WarrantyListResponse.WarrantyListData
import com.app.bharatphoton.services.WarrantyService
import com.google.gson.Gson


class WarrantyLIstPagingSource(private val warrantyService: WarrantyService,private val type:Int) : PagingSource<Int, WarrantyListData>() {
    override fun getRefreshKey(state: PagingState<Int, WarrantyListData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WarrantyListData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = warrantyService.warrantyList(page = pageNumber, limit = pageSize, type = type)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, WarrantyListResponse::class.java)
                    val mList = listResponse.data as List<WarrantyListData>

                    LoadResult.Page(data = mList, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mList.isEmpty()) null else pageNumber + 1)
                } catch (e: Exception) {
                    e.message?.mlog()
                    LoadResult.Error(e)
                }
            } else {
                LoadResult.Error(Exception("Error"))
            }

        } catch (e: Exception) {
            e.message.mlog()
            LoadResult.Error(e)
        }
    }
}