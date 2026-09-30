package com.app.bharatphoton.module.complaintResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterComplaintResponse(
    val code: Int?, val count: Count?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val address: String?,
        val addressId: String?,
        @SerializedName("address_type") val addressType: Int?,
        val categoryId: String?,
        val city: String?,
        val complaintReason: String?,
        val complaintStatus: Int?,
        val countryCode: String?,
        val createdAt: String?,
        val description: String?,
        val firstName: String?,
        @SerializedName("_id") val id: String?,
        val image: List<Image?>?,
        @SerializedName("is_deleted") val isDeleted: Boolean?,
        val lastName: String?,
        val phoneNo: Long?,
        val productDescription: String?,
        val productId: String?,
        val productImage: List<ProductImage?>?,
        val productModelNo: String?,
        val productName: String?,
        val resolveDate: String?,
        val sin: String?,
        val staffId: String?,
        val state: String?,
        val status: Boolean?,
        val streetAddress: String?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?,
        @SerializedName("ZipCode") val zipCode: Int?
    ) : Parcelable {
        @Parcelize
        data class Image(
            @SerializedName("_id") val id: String?, val url: String?
        ) : Parcelable

        @Parcelize
        data class ProductImage(
            @SerializedName("_id") val id: String?, val url: String?
        ) : Parcelable
    }
}