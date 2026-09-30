package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.MONTHLY_REPORT
import com.app.bharatphoton.retrofit.Constants.EndPoints.REPORTS
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportsService {
    @GET(REPORTS)
    suspend fun reports(): Response<JsonObject>

    @GET(MONTHLY_REPORT)
    suspend fun monthlyReport(@Query("orderId") orderId: String, @Query("productId") productId: String): Response<JsonObject>
}