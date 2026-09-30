package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.ADD_QUERY
import com.app.bharatphoton.retrofit.Constants.EndPoints.CHANGE_PASSWORD
import com.app.bharatphoton.retrofit.Constants.EndPoints.CMS_DATA
import com.app.bharatphoton.retrofit.Constants.EndPoints.DELETE_ACCOUNT
import com.app.bharatphoton.retrofit.Constants.EndPoints.FAQ_LIST
import com.app.bharatphoton.retrofit.Constants.EndPoints.FORGET_PASSWORD
import com.app.bharatphoton.retrofit.Constants.EndPoints.LOGIN
import com.app.bharatphoton.retrofit.Constants.EndPoints.REGISTER
import com.app.bharatphoton.retrofit.Constants.EndPoints.RESENDOTP
import com.app.bharatphoton.retrofit.Constants.EndPoints.RESET_PASSWORD
import com.app.bharatphoton.retrofit.Constants.EndPoints.S3
import com.app.bharatphoton.retrofit.Constants.EndPoints.SOCIAL_LOGIN
import com.app.bharatphoton.retrofit.Constants.EndPoints.TROUBLESHOOT_VIDEO
import com.app.bharatphoton.retrofit.Constants.EndPoints.UPDATE_DEVICE_TOKEN
import com.app.bharatphoton.retrofit.Constants.EndPoints.VERIFY_OTP
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthService {
    @POST(REGISTER)
    suspend fun register(@Body request: JsonObject): Response<JsonObject>

    @POST(VERIFY_OTP)
    suspend fun verifyOtp(@Header ("authtoken") token: String, @Body request: JsonObject): Response<JsonObject>

    @POST(LOGIN)
    suspend fun login(@Body request: JsonObject): Response<JsonObject>

    @POST(SOCIAL_LOGIN)
    suspend fun socialLogin(@Body request: JsonObject): Response<JsonObject>

    @POST(RESENDOTP)
    suspend fun resendOtp(@Body request: JsonObject): Response<JsonObject>

    @POST(FORGET_PASSWORD)
    suspend fun forgetPassword(@Body request: JsonObject): Response<JsonObject>

    @GET(S3)
    suspend fun s3(): Response<JsonObject>

    @POST(RESET_PASSWORD)
    suspend fun resetPassword(@Body request: JsonObject): Response<JsonObject>

    @GET(CMS_DATA)
    suspend fun cmsData(@Query("userType") userType: Int, @Query("type") type: String): Response<JsonObject>

    @GET(TROUBLESHOOT_VIDEO)
    suspend fun troubleShootingVideo(@Query("categoryId") categoryId: String, @Query("productId") productId: String): Response<JsonObject>

    @PUT(UPDATE_DEVICE_TOKEN)
    suspend fun updateDeviceToken(@Body request: JsonObject): Response<JsonObject>

    @PUT(DELETE_ACCOUNT)
    suspend fun deleteAccount(@Body request: JsonObject): Response<JsonObject>

    @PUT(CHANGE_PASSWORD)
    suspend fun changePassword(@Body request: JsonObject): Response<JsonObject>

    @GET(FAQ_LIST)
    suspend fun faqList(): Response<JsonObject>

    @POST(ADD_QUERY)
    suspend fun addQuery(@Body request : JsonObject) : Response<JsonObject>

}