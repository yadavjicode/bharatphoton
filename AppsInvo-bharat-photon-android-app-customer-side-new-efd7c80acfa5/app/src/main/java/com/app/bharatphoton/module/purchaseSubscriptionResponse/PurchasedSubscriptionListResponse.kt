package com.app.bharatphoton.module.purchaseSubscriptionResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchasedSubscriptionListResponse(
    val code: Int?, val count: Int?, val data: List<SubscriptionListData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class SubscriptionListData(
        val categoryId: String?,
        val categoryName: String?,
        val createdAt: String?,
        val currencyType: String?,
        val finalTotalAmount: Double?,
        @SerializedName("_id") val id: String?,
        val installationAddressId: String?,
        val installationMetrics: List<InstallationMetric?>?,
        val invoiceUrl: String?,
        val isFieldAvailable: Int?,
        val isUrinalSensorAvilable: Int?,
        val isPlanExpired: Int?,
        val isSitePhotoUploaded: Int?,
        val mode: String?,
        val modelNo: String?,
        val paymentMethod: String?,
        val planType: Int?,
        val price: Int?,
        val productId: String?,
        val productImage: String?,
        val productName: String?,
        val quantity: Int?,
        val sin: String?,
        val subscriptionExpiryDate: String?,
        val subscriptionStartDate: String?,
        val totalCostSaved: Int?,
        val totalWaterSaved: Int?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class InstallationMetric(
            val costSaved: Int?,
            val finNo: String?,
            @SerializedName("_id") val id: String?,
            val sin: String?,
            val subscriptionStartDate: String?,
            val subscriptionExpiryDate: String?,
            val isPlanExpired: Int?,
            val waterSaved: Int?
        ) : Parcelable
    }
}