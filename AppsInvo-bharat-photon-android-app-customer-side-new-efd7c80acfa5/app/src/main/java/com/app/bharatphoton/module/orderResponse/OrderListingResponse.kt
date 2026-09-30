package com.app.bharatphoton.module.orderResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListingResponse(
    val code: Int?, val count: Int?, val title: String, val data: List<OrderListingData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class OrderListingData(
        val coupons: List<Coupon?>?,
        val createdAt: String?,
        val currency: String?,
        val discountedAmount: Double?,
        val finalOrderStatus: Int?,
        val itemCount: Int?,
        val finalTotalAmount: Double?,
        @SerializedName("_id") val id: String?,
        val paymentMethod: String?,
        val products: List<Product?>?,
        val subTotal: Int?,
        val taxAmount: Double?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class Coupon(
            val couponId: String?, val couponType: Int?, @SerializedName("_id") val id: String?, val title: String?
        ) : Parcelable

        @Parcelize
        data class Product(
            val createdAt: String?,
            val description: String?,
            val modelNo: String?,
            val finNo: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            val installationAddressId: String?,
            val isSitePhotoUploaded: Int?,
            val installationPincode: Int?,
            val orderStatus: Int?,
            val price: Int?,
            val productId: String?,
            @SerializedName("product_name") val productName: String?,
            val quantity: Int?,
            val totalPrice: Int?,
            val uuid: String?
        ) : Parcelable
    }
}