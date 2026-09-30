package com.app.bharatphoton.module.cartSubscriptionResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddSubscriptionToCartApiResponse(
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
        val userId: String?,
        @SerializedName("__v")
        val v: Int?
    ) : Parcelable
}