package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.CHECK_INSTALLATION
import com.app.bharatphoton.retrofit.Constants.EndPoints.CHECK_OUT
import com.app.bharatphoton.retrofit.Constants.EndPoints.REMOVE_INSTALLATION
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface CheckOutService {
    @GET(CHECK_OUT)
    suspend fun checkOut(@Query("cartId") cartId: String): Response<JsonObject>

    @POST(CHECK_INSTALLATION)
    suspend fun checkInstallation(@Body request: JsonObject): Response<JsonObject>

    @PATCH(REMOVE_INSTALLATION)
    suspend fun removeInstallation(@Body request: JsonObject): Response<JsonObject>

}