package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PaymentHistoryService {
    @GET(Constants.EndPoints.PAYMENT_HISTORY)
    suspend fun paymentHistory(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): Response<JsonObject>
}