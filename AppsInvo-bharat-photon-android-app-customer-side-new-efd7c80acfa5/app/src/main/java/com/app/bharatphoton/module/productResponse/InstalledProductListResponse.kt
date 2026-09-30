package com.app.bharatphoton.module.productResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstalledProductListResponse(
    val code: Int?, val count: Int?, val `data`: List<InstalledProductData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class InstalledProductData(
        val createdAt: String?,
        @SerializedName("_id") val id: String?,
        val invoiceUrl: String?,
        val isSitePhotoUploaded: Int?,
        val products: List<Product?>?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class Product(
            val categoryName: String?,
            val uuid: String?,
            val costSaved: Double?,
            val description: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            val installationInfo: List<InstallationInfo?>?,
            val modelNo: String?,
            val productId: String?,
            @SerializedName("product_name") val productName: String?,
            val quantity: Int?,
            val isFieldAvailable: Int?,
            val isUrinalSensorAvilable: Int?,
            val remainingWarrantyDays: Double?,
            val warrentyEndDate: String?,
            val warrentyStartDate: String?,
            val waterSaved: Int?
        ) : Parcelable {
            @Parcelize
            data class InstallationInfo(
                val costSaved: Int?,
                @SerializedName("_id") val id: String?,
                val sin: String?,
                val warrentyEndDate: String?,
                val warrentyMonth: String?,
                val warrentyStartDate: String?,
                val waterSaved: Int?
            ) : Parcelable
        }
    }
}