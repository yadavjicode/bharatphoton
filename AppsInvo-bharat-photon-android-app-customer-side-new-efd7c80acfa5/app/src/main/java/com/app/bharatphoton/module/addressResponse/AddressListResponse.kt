package com.app.bharatphoton.module.addressResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddressListResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<AddressListData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class AddressListData(
        val isSelect: Boolean = false,
        val address: String?,
        val addressType: Int?,
        var selectedAddress: String?,
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
        val state: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("ZipCode")
        val zipCode: Int?
    ) : Parcelable
}