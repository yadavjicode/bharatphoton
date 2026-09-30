package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.app.bharatphoton.retrofit.Constants.EndPoints.APPLY_COUPON
import com.app.bharatphoton.retrofit.Constants.EndPoints.COUPON_LIST
import com.app.bharatphoton.retrofit.Constants.EndPoints.REMOVE_COUPON
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CouponService {
    @GET(COUPON_LIST)
    suspend fun getCouponList(@Query("cartId") cartId: String): Response<JsonObject>

    @POST(APPLY_COUPON)
    suspend fun applyCoupon(@Body request: JsonObject): Response<JsonObject>

    @POST(REMOVE_COUPON)

    suspend fun removeCoupon(@Body request: JsonObject) : Response<JsonObject>


}