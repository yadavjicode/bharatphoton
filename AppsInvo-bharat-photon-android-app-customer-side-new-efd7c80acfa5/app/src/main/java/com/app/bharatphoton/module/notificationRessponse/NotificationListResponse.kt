package com.app.bharatphoton.module.notificationRessponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationListResponse(
    val code: Int?, val count: Int?, val `data`: List<NotificationData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class NotificationData(
        val body: String?,
        val createdAt: String?,
        @SerializedName("_id") val id: String?,
        @SerializedName("is_Active") val isActive: Boolean?,
        @SerializedName("is_deleted") val isDeleted: Boolean?,
        val orderId: String?,
        val profileImage: String?,
        val read: Int?,
        val title: String?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("__v") val v: Int?
    ) : Parcelable
}