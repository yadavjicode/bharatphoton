package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.notificationRessponse.NotificationListResponse
import com.app.bharatphoton.module.notificationRessponse.NotificationListResponse.NotificationData
import com.app.bharatphoton.services.NotificationService
import com.google.gson.Gson


class NotificationLIstPagingSource(private val notificationService: NotificationService, private val read: String) : PagingSource<Int, NotificationData>() {
    override fun getRefreshKey(state: PagingState<Int, NotificationData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = notificationService.notificationList(page = pageNumber, limit = pageSize, read = read)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, NotificationListResponse::class.java)
                    val mList = listResponse.data as List<NotificationData>

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