package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.module.wishlistResponse.WishListResponse
import com.app.bharatphoton.services.WishListService
import com.google.gson.Gson

class WishListPagingSource(private val wishListService: WishListService) : PagingSource<Int, WishListResponse.WishListData>() {
    override fun getRefreshKey(state: PagingState<Int, WishListResponse.WishListData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WishListResponse.WishListData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = wishListService.wishList(page = pageNumber, limit = pageSize)

            if (response.isSuccessful) {
                val data = response.body().toString()
                val listResponse = Gson().fromJson(data, WishListResponse::class.java)
                val mlist = listResponse.data as List<WishListResponse.WishListData>
                LoadResult.Page(data = mlist, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mlist.isEmpty()) null else pageNumber + 1)
            } else {
                LoadResult.Error(Exception("Error"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}