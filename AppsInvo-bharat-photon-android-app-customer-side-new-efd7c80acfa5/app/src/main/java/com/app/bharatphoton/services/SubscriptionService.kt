package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.CHECK_SUBSCRIPTION
import com.app.bharatphoton.retrofit.Constants.EndPoints.SUBSCRIPTION_LISTING
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubscriptionService {
    @GET(SUBSCRIPTION_LISTING)
    suspend fun subscriptionListing(@Query("productId") productId: String, @Query("pincode") pincode: String): Response<JsonObject>

    @POST(CHECK_SUBSCRIPTION)
    suspend fun checkSubscription(@Body request: JsonObject): Response<JsonObject>

}