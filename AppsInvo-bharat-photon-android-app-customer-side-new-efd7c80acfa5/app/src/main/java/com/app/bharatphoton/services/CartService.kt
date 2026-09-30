package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.ADD_TO_CART
import com.app.bharatphoton.retrofit.Constants.EndPoints.CART_DETAILS
import com.app.bharatphoton.retrofit.Constants.EndPoints.CART_ITEM_COUNT
import com.app.bharatphoton.retrofit.Constants.EndPoints.REMOVE_FROM_CART
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface CartService {
    @POST(ADD_TO_CART)
    suspend fun addToCart(@Body request: JsonObject): Response<JsonObject>

    @GET(CART_ITEM_COUNT)
    suspend fun cartItemCount(@Query("deviceId") deviceId: String): Response<JsonObject>

    @GET(CART_DETAILS)
    suspend fun cartDetails(@Query("deviceId") deviceId: String): Response<JsonObject>

    @HTTP(method = "DELETE", path = REMOVE_FROM_CART, hasBody = true)
    suspend fun removeFromCart(@Body request: JsonObject): Response<JsonObject>

}