package com.app.bharatphoton.module.warrantyApiResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarrantyListResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<WarrantyListData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class WarrantyListData(
        val categoryName: String?,
        val customerId: String?,
        @SerializedName("_id")
        val id: String?,
        val modelNo: String?,
        val orderId: String?,
        val finNo: String?,
        val uuid: String?,
        val productId: String?,
        val uninstalledProductImage: String?,
        val productImage: List<ProductImage?>?,
        val productName: String?,
        val remainingWarrantyDays: Int?,
        val sin: String?,
        val warrentyEndDate: String?,
        val warrentyMonth: String?,
        val warrentyStartDate: String?
    ) : Parcelable {
        @Parcelize
        data class ProductImage(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}