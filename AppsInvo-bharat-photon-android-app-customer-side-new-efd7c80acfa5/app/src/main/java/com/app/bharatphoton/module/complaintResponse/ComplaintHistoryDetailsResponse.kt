package com.app.bharatphoton.module.complaintResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComplaintHistoryDetailsResponse(
    val code: Int?, val count: Int?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val complaintDetails: ComplaintDetails?, val resolvedDetails: ResolvedDetails?
    ) : Parcelable {
        @Parcelize
        data class ComplaintDetails(
            val categoryId: String?,
            val categoryName: String?,
            val complaintReason: String?,
            val description: String?,
            val complaintStatus: Int?,
            val createdAt: String?,
            @SerializedName("_id") val id: String?,
            val image: List<Image?>?,
            val modelNo: String?,
            val productDescription: String?,
            val productId: String?,
            val productImage: List<ProductImage?>?,
            val productName: String?,
            val sin: String?,
            val staffCountryCode: String?,
            val staffEmail: String?,
            val staffFirstName: String?,
            val staffId: String?,
            val staffImage: String?,
            val staffLastName: String?,
            val staffPhoneNo: Long?,
            val updatedAt: String?,
            val userId: String?,
            val uuid: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable

            @Parcelize
            data class ProductImage(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable
        }

        @Parcelize
        data class ResolvedDetails(
            val batteryVoltage: Int?,
            val costSaved: Int?,
            val customerName: String?,
            val flushTime: Int?,
            @SerializedName("_id") val id: String?,
            val preflush: Int?,
            val range: Int?,
            val remark: String?,
            val resolveAction: String?,
            val resolveDate: String?,
            val sin: String?,
            val totalPayment: Double?,
            val userCount: Int?,
            val uuid: String?,
            val waterSaved: Int?
        ) : Parcelable
    }
}