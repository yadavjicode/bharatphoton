package com.app.bharatphoton.module.addressResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteAddressResponse(
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
        val addressType: Int?,
        val city: String?,
        @SerializedName("country_code")
        val countryCode: String?,
        val createdAt: String?,
        val firstName: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val lastName: String?,
        val phoneNo: Long?,
        val selectedAddress: String?,
        val state: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("ZipCode")
        val zipCode: Int?
    ) : Parcelable
}