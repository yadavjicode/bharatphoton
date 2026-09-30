package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.NOTIFICATION_COUNT
import com.app.bharatphoton.retrofit.Constants.EndPoints.NOTIFICATION_LIST
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {
    @GET(NOTIFICATION_LIST)
    suspend fun notificationList(@Query("page") page: Int, @Query("limit") limit: Int, @Query("read") read: String): Response<JsonObject>

    @GET(NOTIFICATION_COUNT)
    suspend fun notificationCount(): Response<JsonObject>

}