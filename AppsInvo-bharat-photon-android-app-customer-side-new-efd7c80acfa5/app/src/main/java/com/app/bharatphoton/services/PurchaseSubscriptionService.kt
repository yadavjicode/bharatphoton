package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.PURCHASED_SUBSCRIPTION_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.PURCHASE_SUBSCRIPTION
import com.app.bharatphoton.retrofit.Constants.EndPoints.RENEW_PLAN
import com.app.bharatphoton.retrofit.Constants.EndPoints.SUBSCRIPTION_SERVICE_LOG
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_SUBSCRIPTION_PAYMENT
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PurchaseSubscriptionService {

    @POST(PURCHASE_SUBSCRIPTION)
    suspend fun purchaseSubscription(@Body request: JsonObject): Response<JsonObject>

    @POST(VERIFY_SUBSCRIPTION_PAYMENT)
    suspend fun verifySubscriptionPayment(@Body request: JsonObject): Response<JsonObject>

    @GET(PURCHASED_SUBSCRIPTION_LISTING)
    suspend fun purchasedSubscriptionListing(
        @Query("page") page: Int, @Query("limit") limit: Int, @Query("id") id: String, @Query("type") type: Int, @Query("installationId") installationId: String
    ): Response<JsonObject>

    @GET(SUBSCRIPTION_SERVICE_LOG)
    suspend fun subscriptionServiceLog(
        @Query("id") id: String, @Query("page") page: Int, @Query("limit") limit: Int, @Query("date") date: String, @Query("sin") sin: String
    ): Response<JsonObject>

    @GET(RENEW_PLAN)
    suspend fun renewPlan(@Query("id") id: String, @Query("sin") sin: String): Response<JsonObject>

}