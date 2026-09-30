package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReviewService {
    @POST(Constants.EndPoints.ADD_REVIEW)
    suspend fun addReview(@Body request: JsonObject): Response<JsonObject>

    @GET(Constants.EndPoints.REVIEW_LIST)
    suspend fun getReviews(@Query("productId") productId:String,@Query("page") page:Int, @Query("limit") limit:Int): Response<JsonObject>


}