package com.app.bharatphoton.module.authResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SocialLoginResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        @SerializedName("auth_token")
        val authToken: String?,
        val result: Result?
    ) : Parcelable {
        @Parcelize
        data class Result(
            val businessEmail: String?,
            val contactNo: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            val createdAt: String?,
            val deviceId: String?,
            val deviceToken: String?,
            val profileImage: String?,
            val deviceType: String?,
            val facebookId: String?,
            val googleId: String?,
            @SerializedName("_id")
            val id: String?,
            val isActive: Boolean?,
            val isDeleted: Boolean?,
            val isPhoneVerified: Boolean?,
            val isemailVerified: Boolean?,
            val saveMoney: Int?,
            val saveWater: String?,
            val socialType: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val userType: Int?,
            @SerializedName("__v")
            val v: Int?
        ) : Parcelable
    }
}