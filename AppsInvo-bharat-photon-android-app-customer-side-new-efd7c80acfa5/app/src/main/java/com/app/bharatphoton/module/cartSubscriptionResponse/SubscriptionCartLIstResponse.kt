package com.app.bharatphoton.module.cartSubscriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SubscriptionCartLIstResponse(
    val code: Int?,
    val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val description: String?,
        val finalTotalAmount: Double?,
        @SerializedName("_id")
        val id: String?,
        val image: List<Image?>?,
        val mode: String?,
        val planType: Int?,
        val price: Int?,
        val productId: String?,
        val productName: String?,
        val quantity: Int?,
        val rating: Int?,
        val subscriptionId: String?,
        val taxAmount: Int?,
        val totalPrice: Int?,
        val userId: String?
    ) : Parcelable {
        @Parcelize
        data class Image(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}