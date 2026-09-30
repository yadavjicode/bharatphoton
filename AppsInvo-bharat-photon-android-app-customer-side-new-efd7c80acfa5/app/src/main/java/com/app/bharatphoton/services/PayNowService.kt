package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.PAY_NOW
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_ONLINE_PAYMENT
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PayNowService {
    @POST(PAY_NOW)
    suspend fun payNow(@Body request: JsonObject): Response<JsonObject>

    @POST(VERIFY_ONLINE_PAYMENT)
    suspend fun verifyOnlinePayment(@Body request: JsonObject): Response<JsonObject>
}