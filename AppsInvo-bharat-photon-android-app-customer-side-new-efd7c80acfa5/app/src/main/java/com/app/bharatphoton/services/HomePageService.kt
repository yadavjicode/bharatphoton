package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface HomePageService {
    @GET(Constants.EndPoints.HOME_PAGE)
    suspend fun homePage() : Response<JsonObject>
}