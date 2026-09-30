package com.app.bharatphoton.module.productResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SinDetailsServiceLogResponse(
    val code: Int?, val count: Count?, val `data`: List<SinDetailsServiceLogData?>?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class SinDetailsServiceLogData(
        val complaintReason: String?,
        val complaintStatus: Int?,
        val createdAt: String?,
        val description: String?,
        val firstName: String?,
        @SerializedName("_id") val id: String?,
        val lastName: String?,
        var remainingWarrantyDays: Int?,
        val payment: Double?,
        val profileImage: String?,
        val customerName: String?,
        val remark: String?,
        val resolveActionTaken: String?,
        val resolveDate: String?,
        val sin: String?,
        val staffId: String?,
        val uuid: String?
    ) : Parcelable
}