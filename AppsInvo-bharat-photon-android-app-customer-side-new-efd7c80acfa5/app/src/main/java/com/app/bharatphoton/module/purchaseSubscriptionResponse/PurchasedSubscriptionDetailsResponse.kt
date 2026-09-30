package com.app.bharatphoton.module.purchaseSubscriptionResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PurchasedSubscriptionDetailsResponse(
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
        val installationAddress: InstallationAddress?,
        val sinDetails: SinDetails?,
        val subscriptionLisitng: SubscriptionLisitng?
    ) : Parcelable {
        @Parcelize
        data class InstallationAddress(
            val address: String?,
            val city: String?,
            val createdAt: String?,
            @SerializedName("_id") val id: String?,
//            val image: List<String?>?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val orderId: String?,
            val state: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?,
            @SerializedName("ZipCode") val zipCode: Int?
        ) : Parcelable

        @Parcelize
        data class SinDetails(
            val batteryVoltage: Double?,
            val costSaved: Int?,
            val customerName: String?,
            val flushTime: Int?,
            val offDelay: Int?,
            val cutOffDelay: Int?,
            @SerializedName("_id") val id: String?,
            val image: List<Image?>?,
            val isFieldAvailable: Int?,
            val montlyInstallationUrl: String?,
            val preflush: Int?,
            val range: Int?,
            val remark: String?,
            val sin: String?,
            val totalPayment: Int?,
            val userCount: Int?,
            val waterSaved: Int?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable
        }

        @Parcelize
        data class SubscriptionLisitng(
            val address: String?,
            val addressId: String?,
            @SerializedName("address_type") val addressType: Int?,
            val categoryId: String?,
            val categoryName: String?,
            val city: String?,
            val countryCode: String?,
            val createdAt: String?,
            val currencyType: String?,
            val daysRemaining: Int?,
            val features: List<Feature?>?,

            val finalOrderStatus: Int?,
            val finalTotalAmount: Int?,
            val firstName: String?,
            @SerializedName("_id") val id: String?,
            val invoiceUrl: String?,
            val isFieldAvailable: Int?,
            val isUrinalSensorAvilable: Int?,
            val lastName: String?,
            val mode: String?,
            val modelNo: String?,
            val paymentMethod: String?,
            val paymentMode: Int?,
            val paymentStatus: String?,
            val phoneNo: Long?,
            val planType: Int?,
            val price: Int?,
            @SerializedName("ProductDetailsimage") val productDetailsimage: List<ProductDetailsimage?>?,
            val productId: String?,
            val productImage: String?,
            val productName: String?,
            val quantity: Int?,
            val resolveDate: String?,
            val sin: String?,
            val specification: List<Specification?>?,
            val staffId: String?,
            val state: String?,
            val status: Boolean?,
            val subscriptionExpiryDate: String?,
            val subscriptionStartDate: String?,
            val totalPrice: Int?,
            val updatedAt: String?,
            val userId: String?,
            val uuid: String?,
            val youtube: List<Youtube?>?,
            @SerializedName("ZipCode") val zipCode: Int?
        ) : Parcelable {
            @Parcelize
            data class ProductDetailsimage(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable
        }

        @Parcelize
        data class Specification(
            val filedName: String?, @SerializedName("_id") val id: String?, val value: String?
        ) : Parcelable

        @Parcelize
        data class Youtube(
            val demoVideo: String?, @SerializedName("_id") val id: String?, val installationVideo: String?
        ) : Parcelable

        @Parcelize
        data class Feature(
            val description: String?, @SerializedName("_id") val id: String?, val label: String?
        ) : Parcelable
    }
}