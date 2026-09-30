package com.app.bharatphoton.module.profileResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BusinessEditProfileResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val address: String?,
        val city: String?,
        val companyName: String?,
        val contactNo: Long?,
        val contactPerson: String?,
        @SerializedName("country_code")
        val countryCode: String?,
        val createdAt: String?,
        val designation: String?,
        val deviceToken: String?,
//        val deviceType: Any?,
        val email: String?,
        val firstName: String?,
        val gstFile: String?,
        val gstNumber: String?,
        @SerializedName("_id")
        val id: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val isPhoneVerified: Boolean?,
        val isemailVerified: Boolean?,
        val lastName: String?,
        val latitude: String?,
        val longitude: String?,
        val otp: String?,
        val phoneNo: Long?,
        val profileImage: String?,
        val state: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userType: Int?,
        val zipCode: Int?
    ) : Parcelable
}