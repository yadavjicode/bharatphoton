package com.app.bharatphoton.module.authResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UpdateDeviceTokenResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val contactNo: String?,
        @SerializedName("country_code")
        val countryCode: String?,
        val createdAt: String?,
        val deviceId: String?,
        val deviceToken: String?,
        val deviceType: String?,
        val email: String?,
        val facebookId: String?,
        val firstName: String?,
        val googleId: String?,
        @SerializedName("_id")
        val id: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val isPhoneVerified: Boolean?,
        val isemailVerified: Boolean?,
        val lastName: String?,
        val saveMoney: Int?,
        val saveWater: String?,
        val socialType: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userType: Int?
    ) : Parcelable
}