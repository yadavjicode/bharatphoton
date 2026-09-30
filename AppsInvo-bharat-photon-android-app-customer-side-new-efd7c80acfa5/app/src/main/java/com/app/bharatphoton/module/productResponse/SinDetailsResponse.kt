package com.app.bharatphoton.module.productResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SinDetailsResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val complaintDates: List<String?>?,
        val installatedDetails: InstallatedDetails?
    ) : Parcelable {
        @Parcelize
        data class InstallatedDetails(
            val batteryVoltage: Double?,
            val createdAt: String?,
            val categoryName: String?,
            val categoryId: String?,
            val customerId: String?,
            val customerName: String?,
            val finNo: String?,
            val flushTime: Int?,
            val offDelay: Int?,
            val cutOffDelay: Int?,
            val waterSaved: Int?,
            val costSaved: Double?,
            @SerializedName("_id")
            val id: String?,
            val image: List<Image?>?,
            @SerializedName("is_deleted")
            val isDeleted: Boolean?,
            val isOtpVerfied: Boolean?,
            val modelNo: String?,
            val orderId: String?,
            val otp: String?,
            val preflush: Int?,
            val productId: String?,
            val productImage: List<ProductImage?>?,
            val productName: String?,
            val range: Int?,
            val remark: String?,
            val sin: String?,
            val remainingWarrantyDays: Int?,
            val isFieldAvailable: Int?,
            val isUrinalSensorAvilable: Int?,
            val staffId: String?,
            val status: Boolean?,
            val totalPayment: Int?,
            val montlyInstallationUrl: String?,
            val updatedAt: String?,
            val userCount: Int?,
            val warrentyEndDate: String?,
            val warrentyMonth: String?,
            val warrentyStartDate: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id")
                val id: String?,
                val url: String?
            ) : Parcelable

            @Parcelize
            data class ProductImage(
                @SerializedName("_id")
                val id: String?,
                val url: String?
            ) : Parcelable
        }
    }
}