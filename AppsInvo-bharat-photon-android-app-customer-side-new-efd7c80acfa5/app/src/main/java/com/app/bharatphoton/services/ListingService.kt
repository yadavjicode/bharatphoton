package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.app.bharatphoton.retrofit.Constants.EndPoints.CATEGORY_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.CITY_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.PRODUCT_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.STATE_LISTING
import com.app.bharatphoton.retrofit.Constants.EndPoints.SUB_CATEGORY_LISTING
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ListingService {

    @GET(CATEGORY_LISTING)
    suspend fun categoryListing(): Response<JsonObject>

    @GET(SUB_CATEGORY_LISTING)
    suspend fun subCategoryListing(@Query("categoryId") categoryId: String): Response<JsonObject>

    @GET(STATE_LISTING)
    suspend fun stateListing(): Response<JsonObject>

    @GET(CITY_LISTING)
    suspend fun cityListing(@Query("stateName") stateName: String): Response<JsonObject>

    @GET(PRODUCT_LISTING)
    suspend fun productListing(@Query("categoryId") categoryId:String) : Response<JsonObject>

}