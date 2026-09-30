package com.app.bharatphoton.module.profileResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class VerifyPhoneEmailResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val appleId: String?,
        val businessEmail: String?,
        val businessPhoneNo: Long?,
        val companyName: String?,
        val contactNo: Int?,
        val contactPerson: String?,
        val contactPersoncountryCode: String?,
        @SerializedName("country_code")
        val countryCode: String?,
        val createdAt: String?,
        val designation: String?,
        val deviceId: String?,
        val deviceToken: String?,
        val deviceType: String?,
        val facebookId: String?,
        val googleId: String?,
        val gstFile: String?,
        val gstNumber: String?,
        val token: String?,
        @SerializedName("_id")
        val id: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val isPhoneVerified: Boolean?,
        val isemailVerified: Boolean?,
        val otp: String?,
        val password: String?,
        val phoneNo: String?,
        val saveMoney: Int?,
        val saveWater: String?,
        val secondayPhoneNo: String?,
        val status: Boolean?,
        val tempEmail: String?,
        val tempPhoneNo: String?,
        @SerializedName("tempcountry_code")
        val tempcountryCode: String?,
        val updatedAt: String?,
        val userType: Int?,
        @SerializedName("__v")
        val v: Int?
    ) : Parcelable
}