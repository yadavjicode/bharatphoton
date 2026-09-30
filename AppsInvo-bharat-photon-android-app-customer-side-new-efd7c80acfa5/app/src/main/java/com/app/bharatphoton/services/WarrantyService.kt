package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.AVAILABLE_WARRANTY
import com.app.bharatphoton.retrofit.Constants.EndPoints.RENEW_WARRANTY
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_WARRANTY_PAYMENT
import com.app.bharatphoton.retrofit.Constants.EndPoints.WARRANTY_LIST
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WarrantyService {
    @GET(AVAILABLE_WARRANTY)
    suspend fun availableWarranty(@Query("productId") productId: String): Response<JsonObject>

    @GET(WARRANTY_LIST)
    suspend fun warrantyList(@Query("page") page: Int, @Query("limit") limit: Int, @Query("type") type: Int): Response<JsonObject>

    @POST(RENEW_WARRANTY)
    suspend fun renewWarranty(@Body request: JsonObject): Response<JsonObject>

    @POST(VERIFY_WARRANTY_PAYMENT)
    suspend fun verifyWarrantyPayment(@Body request: JsonObject): Response<JsonObject>

}