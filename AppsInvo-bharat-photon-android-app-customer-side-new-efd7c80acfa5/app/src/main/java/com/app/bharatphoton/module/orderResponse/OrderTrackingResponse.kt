package com.app.bharatphoton.module.orderResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OrderTrackingResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val address: Address?,
//        val coupons: List<String?>?,
        val createdAt: String?,
        val currency: String?,
        val discountedAmount: Int?,
        val finalOrderStatus: Int?,
        val finalTotalAmount: Int?,
        val installationAddress: InstallationAddress?,
        val invoiceUrl: String?,
        val orderId: String?,
        val paymentMethod: String?,
        val product: Product?,
        val subTotal: Int?,
        val taxAmount: Int?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class Address(
            val address: String?,
            @SerializedName("address_type")
            val addressType: Int?,
            val city: String?,
            val countryCode: String?,
            val firstName: String?,
            val lastName: String?,
            val phoneNo: Long?,
            val state: String?,
            @SerializedName("ZipCode")
            val zipCode: Int?
        ) : Parcelable

        @Parcelize
        data class InstallationAddress(
            val address: String?,
            val city: String?,
            @SerializedName("_id")
            val id: String?,
            val orderId: String?,
            val state: String?,
            val userId: String?,
            @SerializedName("ZipCode")
            val zipCode: Int?
        ) : Parcelable

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