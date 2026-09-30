package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPurchaseSubscriptionListResponse
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPurchaseSubscriptionListResponse.RenewPurchaseSubscriptionData
import com.app.bharatphoton.services.PurchaseSubscriptionService
import com.google.gson.Gson


class RenewPurchasedSubscriptionListingPagingSource(private val purchaseSubscriptionService: PurchaseSubscriptionService, private val id: String, val type: Int, val installationId: String) :
    PagingSource<Int, RenewPurchaseSubscriptionData>() {
    override fun getRefreshKey(state: PagingState<Int, RenewPurchaseSubscriptionData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RenewPurchaseSubscriptionData> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = purchaseSubscriptionService.purchasedSubscriptionListing(page = pageNumber, limit = pageSize, id = id, type = type, installationId = installationId)

            if (response.isSuccessful) {
                try {
                    val data = response.body().toString()
                    val listResponse = Gson().fromJson(data, RenewPurchaseSubscriptionListResponse::class.java)
                    val mList = listResponse.data as List<RenewPurchaseSubscriptionData>

                    LoadResult.Page(data = mList, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mList.isEmpty()) null else pageNumber + 1)
                } catch (e: Exception) {
                    e.message.mlog()
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