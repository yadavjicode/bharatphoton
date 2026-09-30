package com.app.bharatphoton.module.orderResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CreateOrderResponse(
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
        val cartId: String?,
        val city: String?,
        val razorpayOrderId: String?,
        val countryCode: String?,
        val createdAt: String?,
        val currency: String?,
        val discountedAmount: Double?,
        val finalTotalAmount: Double?,
        val firstName: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val lastName: String?,
        val paymentMode: Int?,
        val paymentMethod: String?,
        val phoneNo: Int?,
        val products: List<Product?>?,
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
            val assignDate: String?,
            val deliveredProductDate: String?,
            val description: String?,
            val dispatchedProductDate: String?,
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