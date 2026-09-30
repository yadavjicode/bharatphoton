package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.INSTALLED_PRODUCT_LIST
import com.app.bharatphoton.retrofit.Constants.EndPoints.PRODUCT_DETAILS
import com.app.bharatphoton.retrofit.Constants.EndPoints.PRODUCT_LIST
import com.app.bharatphoton.retrofit.Constants.EndPoints.SIN_DETAILS
import com.app.bharatphoton.retrofit.Constants.EndPoints.SIN_DETAILS_SERVICE_LOG
import com.app.bharatphoton.retrofit.Constants.EndPoints.SUB_CAT_PRODUCT_LIST
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET(PRODUCT_DETAILS)
    suspend fun getProductDetails(@Query("id") id: String, @Query("deviceId") deviceId: String): Response<JsonObject>

    @GET(PRODUCT_LIST)
    suspend fun productList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("categoryId") categoryId: String,
        @Query("subcategoryId") subcategoryId: String,
        @Query("search") search: String,
        @Query("deviceId") deviceId: String
    ): Response<JsonObject>

    @GET(SUB_CAT_PRODUCT_LIST)
    suspend fun subCatProductList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("categoryId") categoryId: String,
        @Query("subcategoryId") subcategoryId: String,
        @Query("deviceId") deviceId: String
    ): Response<JsonObject>

    @GET(INSTALLED_PRODUCT_LIST)
    suspend fun installedProductList(
        @Query("page") page: Int, @Query("limit") limit: Int, @Query("id") id: String, @Query("orderId") orderId: String
    ): Response<JsonObject>

    @GET(SIN_DETAILS)
    suspend fun sinDetails(@Query("orderId") orderId: String, @Query("productId") productId: String, @Query("installationId") installationId: String): Response<JsonObject>

    @GET(SIN_DETAILS_SERVICE_LOG)
    suspend fun sinDetailsServiceLog(
        @Query("orderId") orderId: String, @Query("productId") productId: String, @Query("page") page: Int, @Query("limit") limit: Int, @Query("date") date: String, @Query("sin") sin: String
    ): Response<JsonObject>

}