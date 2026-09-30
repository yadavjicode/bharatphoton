package com.app.bharatphoton.module.subcriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SubscriptionListResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<SubscriptionList?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class SubscriptionList(
        val createdAt: String?,
        val currencyType: String?,
        val description: String?,
        val duration: String?,
        @SerializedName("_id")
        val id: String?,
        val image: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val price: Int?,
        val status: Boolean?,
        val title: String?,
        val updatedAt: String?
    ) : Parcelable
}