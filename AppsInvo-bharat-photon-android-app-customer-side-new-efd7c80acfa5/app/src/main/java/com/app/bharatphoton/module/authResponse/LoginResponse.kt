package com.app.bharatphoton.module.authResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    val code: Int?, val count: Count?, val data: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        @SerializedName("auth_token") val authToken: String?, val result: Result?
    ) : Parcelable

    @Parcelize
    data class Result(
        val address: String?,
        val appleId: String?,
        val businessEmail: String?,
        val businessPhoneNo: Long?,
        val city: String?,
        val companyName: String?,
        val contactNo: Long?,
        val contactPerson: String?,
        val contactPersoncountryCode: String?,
        @SerializedName("country_code") val countryCode: String?,
        val createdAt: String?,
        val designation: String?,
        val deviceId: String?,
        val deviceToken: String?,
        val deviceType: String?,
        val facebookId: String?,
        val googleId: String?,
        val gstFile: String?,
        val gstNumber: String?,
        @SerializedName("_id") val id: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val isPhoneVerified: Boolean?,
        val isemailVerified: Boolean?,
        val otp: String?,
        val password: String?,
        val phoneNo: String?,
        val profileImage: String?,
        val saveMoney: Int?,
        val saveWater: String?,
        val secondayPhoneNo: Long?,
        val state: String?,
        val status: Boolean?,
        val tempEmail: String?,
        val tempPhoneNo: String?,
        @SerializedName("tempcountry_code") val tempcountryCode: String?,
        val updatedAt: String?,
        val userType: Int?,
        val zipCode: Int?
    ) : Parcelable
}