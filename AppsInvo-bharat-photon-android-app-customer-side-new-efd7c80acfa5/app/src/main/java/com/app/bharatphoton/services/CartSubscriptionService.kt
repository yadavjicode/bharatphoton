package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.ADD_SUBSCRIPTION_TO_CART
import com.app.bharatphoton.retrofit.Constants.EndPoints.SUBSCRIPTION_CART_LIST
import com.app.bharatphoton.retrofit.Constants.EndPoints.UPDATE_QUANTITY
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CartSubscriptionService {
    @POST(ADD_SUBSCRIPTION_TO_CART)
    suspend fun addSubscriptionToCart(@Body request: JsonObject): Response<JsonObject>

    @GET(SUBSCRIPTION_CART_LIST)
    suspend fun subscriptionCartList(@Query("id") id: String): Response<JsonObject>

    @PUT(UPDATE_QUANTITY)
    suspend fun updateQuantity(@Body request: JsonObject): Response<JsonObject>

}