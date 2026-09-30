package com.app.bharatphoton.module.purchaseSubscriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RenewPurchaseSubscriptionListResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<RenewPurchaseSubscriptionData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class RenewPurchaseSubscriptionData(
        val categoryId: String?,
        val categoryName: String?,
        val orderId: String?,
        val createdAt: String?,
        val customerId: String?,
        @SerializedName("_id")
        val id: String?,
        val isFieldAvailable: Int?,
        val remainingDays: Int?,
        val modelNo: String?,
        val productId: String?,
        val productImage: List<ProductImage?>?,
        val productName: String?,
        val sin: String?,
        val subscriptionExpiryDate: String?,
        val subscriptionMode: String?,
        val subscriptionStartDate: String?,
        val subscriptionuuid: String?,
        val updatedAt: String?
    ) : Parcelable {
        @Parcelize
        data class ProductImage(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}