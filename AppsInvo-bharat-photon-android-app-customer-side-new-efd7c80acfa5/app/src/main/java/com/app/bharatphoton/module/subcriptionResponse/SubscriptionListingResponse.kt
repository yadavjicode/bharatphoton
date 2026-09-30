package com.app.bharatphoton.module.subcriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SubscriptionListingResponse(
    val code: Int?,
    val count: Count?,
    val `data`: List<SubscriptionData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class SubscriptionData(
        val createdAt: String?,
        val currencyType: String?,
        val description: String?,
        @SerializedName("_id")
        val id: String?,
        val image: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val mode: String?,
        val pincode: List<Int?>?,
        val planType: Int?,
        val price: Int?,
        val productDescription: String?,
        val categoryName: String?,
        val modelNo: String?,
        val productId: String?,
        val productImage: List<ProductImage?>?,
        val productName: String?,
        val products: List<String?>?,
        val status: Boolean?,
        val title: String?,
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