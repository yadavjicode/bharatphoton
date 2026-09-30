package com.app.bharatphoton.services

import com.app.bharatphoton.retrofit.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AddressService {
    @GET(Constants.EndPoints.ADDRESS_LIST)
    suspend fun addressList(@Query("id") id: String): Response<JsonObject>

    @POST(Constants.EndPoints.ADD_ADDRESS)
    suspend fun addAddress(@Body request: JsonObject): Response<JsonObject>

    @DELETE(Constants.EndPoints.DELETE_ADDRESS)
    suspend fun deleteAddress(@Path("id") id: String): Response<JsonObject>

    @PUT(Constants.EndPoints.UPDATE_DELIVERY_ADDRESS)
    suspend fun updateDeliveryAddress(@Body request: JsonObject): Response<JsonObject>

    @POST(Constants.EndPoints.ADD_INSTALLATION_ADDRESS)
    suspend fun addInstallationAddress(@Body request: JsonObject): Response<JsonObject>

}