package com.app.bharatphoton.module.payNowResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PayNowResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val address: String?,
        val addressId: String?,
        @SerializedName("address_type")
        val addressType: Int?,
        val amountInPaisa: Int?,
        val cartId: String?,
        val city: String?,
        val countryCode: String?,
//        val coupons: List<String?>?,
        val createdAt: String?,
        val currency: String?,
        val discountedAmount: Int?,
        val finalOrderStatus: Int?,
        val finalTotalAmount: Double?,
        val firstName: String?,
        @SerializedName("_id")
        val id: String?,
        val invoiceUrl: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val lastName: String?,
        val paymentMethod: String?,
        val paymentMode: Int?,
        val paymentStatus: String?,
        val phoneNo: Long?,
        val products: List<Product?>?,
        val razorpayOrderId: String?,
        val state: String?,
        val status: Boolean?,
        val subTotal: Int?,
        val taxAmount: Double?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?,
        @SerializedName("__v")
        val v: Int?,
        @SerializedName("ZipCode")
        val zipCode: Int?
    ) : Parcelable {
        @Parcelize
        data class Product(
            val deliveredProductDate: String?,
            val description: String?,
            val dispatchedProductDate: String?,
            val finNo: String?,
            @SerializedName("_id")
            val id: String?,
            val image: String?,
            val installationPincode: String?,
            val installationPrice: Int?,
            val installationStatus: Int?,
            val modelNo: String?,
            val orderStatus: Int?,
            val pendingProductDate: String?,
            val price: Int?,
            val processedProductDate: String?,
            val productId: String?,
            @SerializedName("product_name")
            val productName: String?,
            val quantity: Int?,
            val staffId: String?,
            val totalPrice: Int?,
            val warrentyEndDate: String?,
            val warrentyStartDate: String?
        ) : Parcelable
    }
}