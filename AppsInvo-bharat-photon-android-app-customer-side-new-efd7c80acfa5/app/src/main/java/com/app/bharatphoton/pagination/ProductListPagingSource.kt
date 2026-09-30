package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.productResponse.ProductListingForSubcategory
import com.app.bharatphoton.module.productResponse.ProductListingForSubcategory.ProductListForSubscriptionData.Subcategories
import com.app.bharatphoton.services.ProductService
import com.google.gson.Gson

class ProductListPagingSource(
    private val productService: ProductService, private val categoryId: String, private val deviceId: String, private val subcategoryId: String, private val search: String
) : PagingSource<Int, Subcategories>() {
    override fun getRefreshKey(state: PagingState<Int, Subcategories>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.nextKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Subcategories> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = productService.productList(page = pageNumber, limit = pageSize, categoryId = categoryId, subcategoryId, search = search, deviceId = deviceId)

            if (response.isSuccessful) {
                val data = response.body().toString()
                val listResponse = Gson().fromJson(data, ProductListingForSubcategory::class.java)
                val mlist = listResponse.data?.subcategoriesList as List<Subcategories>
                LoadResult.Page(data = mlist, prevKey = if (pageNumber == 1) null else pageNumber - 1, nextKey = if (mlist.isEmpty()) null else pageNumber + 1)
            } else {
                "Error".mlog()
                LoadResult.Error(Exception("Error"))
            }

        } catch (e: Exception) {
            e.message.mlog()
            LoadResult.Error(e)
        }
    }
}
