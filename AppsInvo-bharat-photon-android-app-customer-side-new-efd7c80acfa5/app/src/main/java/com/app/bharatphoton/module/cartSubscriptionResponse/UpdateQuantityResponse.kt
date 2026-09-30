package com.app.bharatphoton.module.cartSubscriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UpdateQuantityResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val productId: String?,
        val quantity: Int?,
        val status: Boolean?,
        val subscriptionId: String?,
        val updatedAt: String?,
        val totalAmount: Double?,
        val userId: String?
    ) : Parcelable
}