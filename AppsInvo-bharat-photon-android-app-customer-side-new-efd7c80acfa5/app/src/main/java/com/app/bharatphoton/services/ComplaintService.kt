package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants.EndPoints.COMPLAINT_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.REGISTER_COMPLAINT
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ComplaintService {

    @POST(REGISTER_COMPLAINT)
    suspend fun registerComplaint(
        @Body request: JsonObject
    ): Response<JsonObject>

    @GET(COMPLAINT_LISTING)
    suspend fun complaintListing(
        @Query("page") page: Int, @Query("limit") limit: Int, @Query("id") id: String
    ): Response<JsonObject>

}