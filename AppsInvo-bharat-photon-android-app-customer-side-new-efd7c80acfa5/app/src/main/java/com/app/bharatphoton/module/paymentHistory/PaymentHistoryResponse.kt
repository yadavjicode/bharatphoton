package com.app.bharatphoton.module.paymentHistory


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentHistoryResponse(
    val code: Int?, val count: Int?, val `data`: List<PaymentHistoryData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class PaymentHistoryData(
        val createdAt: String?,
        val currency: String?,
        val finalTotalAmount: Double?,
        @SerializedName("_id") val id: String?,
        val log: String?,
        val paymentMethod: String?,
        val invoiceUrl: String?,
        val receiptUrl: String?,
        val paymentStatus: String?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable
}