package com.app.bharatphoton.module.complaintResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ComplaintHistoryResponse(
    val code: Int?,
    val count: Count?,
    val `data`: List<ComplaintHistoryData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class ComplaintHistoryData(
        val categoryId: String?,
        val categoryName: String?,
        val complaintReason: String?,
        val description: String?,
        val complaintStatus: Int?,
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val image: List<Image?>?,
        val modelNo: String?,
        val productDescription: String?,
        val productId: String?,
        val productImage: List<ProductImage?>?,
        val productName: String?,
        val sin: String?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
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