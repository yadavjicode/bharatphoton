package com.app.bharatphoton.module.addressResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddInstallationAddressResponse(
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
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val image: List<Image?>?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val orderId: String?,
        val state: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("ZipCode")
        val zipCode: Int?
    ) : Parcelable {
        @Parcelize
        data class Image(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}