package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WishListService {
    @POST(Constants.EndPoints.ADD_TO_WISHLIST)
    suspend fun addToWishList(@Body request:JsonObject) : Response<JsonObject>

    @GET(Constants.EndPoints.WISHLIST)
    suspend fun wishList(@Query("page") page:Int, @Query("limit") limit:Int) : Response<JsonObject>

    @DELETE(Constants.EndPoints.DELETE_WISHLIST)
    suspend fun deleteWishList(@Path("id") id:String) : Response<JsonObject>

}