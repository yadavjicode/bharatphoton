package com.app.bharatphoton.module.purchaseSubscriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PurchaseSubscriptionResponse(
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
        val uuid: String?,
        val addressId: String?,
        @SerializedName("address_type")
        val addressType: Int?,
        val amountInPaisa: Int?,
        val categoryName: String?,
        val city: String?,
        val countryCode: String?,
        val createdAt: String?,
        val currencyType: String?,
        val finalOrderStatus: Int?,
        val finalTotalAmount: Int?,
        val firstName: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val lastName: String?,
        val mode: String?,
        val modelNo: String?,
        val paymentMethod: String?,
        val paymentMode: Int?,
        val paymentStatus: String?,
        val phoneNo: Long?,
        val planType: Int?,
        val price: Int?,
        val productId: String?,
        val productImage: String?,
        val productName: String?,
        val quantity: Int?,
        val razorpayOrderId: String?,
        val sin: String?,
        val state: String?,
        val status: Boolean?,
        val subscriptionExpiryDate: String?,
        val subscriptionId: String?,
        val subscriptionStartDate: String?,
        val taxAmount: Int?,
        val totalPrice: Int?,
        val updatedAt: String?,
        val userId: String?,
        @SerializedName("__v")
        val v: Int?,
        @SerializedName("ZipCode")
        val zipCode: Int?
    ) : Parcelable
}