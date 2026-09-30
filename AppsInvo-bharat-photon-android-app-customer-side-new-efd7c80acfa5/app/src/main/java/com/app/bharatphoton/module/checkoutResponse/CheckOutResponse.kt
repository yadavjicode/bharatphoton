package com.app.bharatphoton.module.checkoutResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckOutResponse(
    val code: Int?, val count: Int?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val address: Address?, val cart: Cart?, val coupon: Coupon?, val priceSummary: PriceSummary?
    ) : Parcelable {
        @Parcelize
        data class Address(
            val address: String?,
            val addressType: Int?,
            val city: String?,
            @SerializedName("country_code") val countryCode: String?,
            val createdAt: String?,
            val firstName: String?,
            @SerializedName("_id") val id: String?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val lastName: String?,
            val phoneNo: Long?,
            val selectedAddress: String?,
            val state: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?,
            @SerializedName("ZipCode") val zipCode: Int?
        ) : Parcelable

        @Parcelize
        data class Cart(
            var isClicked:Int = 0,
            val createdAt: String?,
            val deviceId: String?,
            @SerializedName("_id") val id: String?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val products: List<Product?>?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?
        ) : Parcelable {
            @Parcelize
            data class Product(
                val image: List<Image?>?,
                var installationPrice: String?,
                val price: Int?,
                @SerializedName("_id") val id: String?,
                val priceToDisplay: Int?,
                val productId: String?,
                val productName: String?,
                val quantity: Int?
            ) : Parcelable {
                @Parcelize
                data class Image(
                    @SerializedName("_id") val id: String?, val url: String?
                ) : Parcelable
            }
        }

        @Parcelize
        data class Coupon(
            val couponCodes: List<String?>?
        ) : Parcelable

        @Parcelize
        data class PriceSummary(
            val discountAmount: Int?, val finalTotalAmount: Double?, val finalTotalAmountAfterDiscount: Double?, val taxAmount: Double?, val totalAmount: Double?
        ) : Parcelable
    }

}