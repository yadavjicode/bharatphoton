package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.CHANGE_EMAIL_PHONE
import com.app.bharatphoton.retrofit.Constants.EndPoints.EDIT_USER
import com.app.bharatphoton.retrofit.Constants.EndPoints.USER_PROFILE
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_EMAIL_PHONE
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface ProfileService {
    @GET(USER_PROFILE)
    suspend fun getUserProfile(): Response<JsonObject>

    @PATCH(EDIT_USER)
    suspend fun editUser(@Body request: JsonObject): Response<JsonObject>

    @PUT(CHANGE_EMAIL_PHONE)
    suspend fun changeEmailPhone(@Body request: JsonObject): Response<JsonObject>

    @PUT(VERIFY_EMAIL_PHONE)
    suspend fun verifyEmailPhone(@Body request: JsonObject): Response<JsonObject>

}