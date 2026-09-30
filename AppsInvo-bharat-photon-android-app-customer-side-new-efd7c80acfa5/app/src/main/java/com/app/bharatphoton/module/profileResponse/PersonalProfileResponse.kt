package com.app.bharatphoton.module.profileResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PersonalProfileResponse(
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
        val deviceToken: String?,
//        val deviceType: Any?,
        val email: String?,
        val zipCode: String?,
        val firstName: String?,
        val state: String?,
        @SerializedName("_id")
        val id: String?,
        val city: String?,
        val companyName: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val isPhoneVerified: Boolean?,
        val isemailVerified: Boolean?,
        val lastName: String?,
        val otp: String?,
        val phoneNo: String?,
        val businessPhoneNo: String?,
        val businessEmail: String?,
        val address: String?,
        val profileImage: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userType: Int?,
        @SerializedName("__v")
        val v: Int?
    ) : Parcelable
}