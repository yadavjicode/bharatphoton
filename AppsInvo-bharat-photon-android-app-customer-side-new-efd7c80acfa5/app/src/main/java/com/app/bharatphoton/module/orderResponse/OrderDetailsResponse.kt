package com.app.bharatphoton.module.orderResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDetailsResponse(
    val code: Int?, val count: Count?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val installastionAddrress: InstallastionAddrress?, val orderDetails: OrderDetails?
    ) : Parcelable {
        @Parcelize
        data class InstallastionAddrress(
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
        data class OrderDetails(
            val address: String?,
            val addressId: String?,
            @SerializedName("address_type") val addressType: Int?,
            val amountInPaisa: Int?,
            val cartId: String?,
            val city: String?,
            val countryCode: String?,
            val coupons: List<Coupon?>?,
            val createdAt: String?,
            val currency: String?,
            val discountedAmount: Double?,
            val finalOrderStatus: Int?,
            val finalTotalAmount: Double?,
            val firstName: String?,
            @SerializedName("_id") val id: String?,
            val invoiceUrl: String?,
            val receiptUrl: String?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val lastName: String?,
            val paymentMethod: String?,
            val paymentMode: Int?,
            val paymentStatus: String?,
            val phoneNo: Long?,
            val products: List<Product?>?,
            val razorpayOrderId: String?,
            val state: String?,
            val status: Boolean?,
            val subTotal: Double?,
            val taxAmount: Double?,
            val updatedAt: String?,
            val userId: String?,
            val uuid: String?,
            @SerializedName("ZipCode") val zipCode: Int?
        ) : Parcelable {
            @Parcelize
            data class Product(
                val assignDate: String?,
                val categoryName: String?,
                val deliveredProductDate: String?,
                val description: String?,
                val dispatchedProductDate: String?,
                val finNo: String?,
                @SerializedName("_id") val id: String?,
                val image: String?,
                val installationPincode: Int?,
                val installationPrice: Int?,
                val installationStatus: Int?,
                val modelNo: String?,
                val orderStatus: Int?,
                val pendingProductDate: String?,
                val price: Int?,
                val processedProductDate: String?,
                val productId: String?,
                @SerializedName("product_name") val productName: String?,
                val quantity: Int?,
                val staffId: String?,
                val totalPrice: Int?,
                val warrentyEndDate: String?,
                val warrentyStartDate: String?
            ) : Parcelable

            @Parcelize
            data class Coupon(
                val couponId: String?,
                val couponType: Int?,
                @SerializedName("_id")
                val id: String?,
                val title: String?
            ) : Parcelable

        }
    }
}