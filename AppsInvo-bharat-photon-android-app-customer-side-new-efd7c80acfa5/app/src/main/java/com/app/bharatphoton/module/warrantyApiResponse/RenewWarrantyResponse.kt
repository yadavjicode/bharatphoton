package com.app.bharatphoton.module.warrantyApiResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RenewWarrantyResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val amountInPaisa: Int?,
        val createdAt: String?,
        val currency: String?,
        val description: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val mode: String?,
        val paymentMode: Int?,
        val paymentStatus: String?,
        val previousOrderId: String?,
        val productId: String?,
        val razorpayOrderId: String?,
        val status: Boolean?,
        val subTotal: Int?,
        val title: String?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("__v")
        val v: Int?,
        val warrentyId: String?
    ) : Parcelable
}