package com.app.bharatphoton.module.purchaseSubscriptionResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubscriptionServiceLog(
    val code: Int?, val count: Int?, val `data`: List<SubscriptionServiceData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class SubscriptionServiceData(
        val complaintReason: String?,
        val complaintStatus: Int?,
        val createdAt: String?,
        val firstName: String?,
        @SerializedName("_id") val id: String?,
        val lastName: String?,
        val description: String?,
        val payment: Double?,
        val profileImage: String?,
        val resolveActionTaken: String?,
        val resolveDate: String?,
        val sin: String?,
        val customerName: String?,
        val remark: String?,
        val remainingDays: Int?,
        val staffId: String?,
        val uuid: String?
    ) : Parcelable
}