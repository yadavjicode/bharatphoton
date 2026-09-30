package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.CANCEL_ORDER
import com.app.bharatphoton.retrofit.Constants.EndPoints.CREATE_ORDER
import com.app.bharatphoton.retrofit.Constants.EndPoints.ORDER_DETAILS
import com.app.bharatphoton.retrofit.Constants.EndPoints.ORDER_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.RE_ORDER
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_PAYMENT
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderService {
    @POST(CREATE_ORDER)
    suspend fun createOrder(@Body request: JsonObject): Response<JsonObject>

    @GET(ORDER_LISTING)
    suspend fun orderListing(@Query("page") page: Int, @Query("limit") limit: Int, @Query("orderStatus") status: Int): Response<JsonObject>

    @GET(ORDER_DETAILS)
    suspend fun orderDetails(@Query("orderId") orderId: String): Response<JsonObject>

    @GET(ORDER_DETAILS)
    suspend fun orderTracking(@Query("id") orderId: String): Response<JsonObject>

    @POST(VERIFY_PAYMENT)
    suspend fun verifyPayment(@Body request: JsonObject): Response<JsonObject>

    @POST(CANCEL_ORDER)
    suspend fun cancelOrder(@Body request: JsonObject): Response<JsonObject>

    @POST(RE_ORDER)
    suspend fun reOrder(@Body request: JsonObject): Response<JsonObject>

}