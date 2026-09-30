package com.app.bharatphoton.module.cartResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddToCartResponse(
    val code: Int?,
    val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val addressId: String?,
        val coupons: List<String?>?,
        val createdAt: String?,
        val deviceId: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val products: List<Product?>?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("__v")
        val v: Int?
    ) : Parcelable {
        @Parcelize
        data class Product(
            @SerializedName("_id")
            val id: String?,
            val installationPincode: String?,
            val productId: String?,
            val quantity: Int?
        ) : Parcelable
    }
}