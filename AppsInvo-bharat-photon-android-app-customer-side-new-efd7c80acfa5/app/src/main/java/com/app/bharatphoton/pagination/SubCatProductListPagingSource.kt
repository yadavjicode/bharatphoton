package com.app.bharatphoton.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.module.productResponse.ViewAllProductListResponse
import com.app.bharatphoton.module.productResponse.ViewAllProductListResponse.ViewAllProductListData.Product
import com.app.bharatphoton.services.ProductService
import com.google.gson.Gson

class SubCatProductListPagingSource(
    private val productService: ProductService, private val categoryId: String, private val deviceId: String, private val subcategoryId: String
) : PagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int?{
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val pageNumber = params.key ?: 1
            val pageSize = if (params.loadSize > 10) 10 else params.loadSize

            val response = productService.subCatProductList(page = pageNumber, limit = pageSize, categoryId = categoryId, subcategoryId, deviceId = deviceId)

            if (response.isSuccessful) {
                val data = response.body().toString()
                val listResponse = Gson().fromJson(data, ViewAllProductListResponse::class.java)
                val mlist = listResponse.data?.productList as List<Product>
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
