package com.app.bharatphoton.module.cartResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartDetailsResponse(
    val code: Int?,
    val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val address: Address?,
        val cart: Cart?
    ) : Parcelable {
        @Parcelize
        data class Address(
            val address: String?,
            val addressType: Int?,
            val city: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            val createdAt: String?,
            val firstName: String?,
            @SerializedName("_id")
            val id: String?,
            @SerializedName("is_deleted")
            val isDeleted: Boolean?,
            val lastName: String?,
            val phoneNo: Int?,
            val selectedAddress: String?,
            val state: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?,
            @SerializedName("ZipCode")
            val zipCode: Int?
        ) : Parcelable

        @Parcelize
        data class Cart(
            val deviceId: String?,
            val discountAmount: Double?,
            val finalTotalAmount: Double?,
            val finalTotalAmountAfterDiscount: Double?,
            @SerializedName("_id")
            val id: String?,
            val products: List<Product?>?,
            val taxAmount: Double?,
            val totalAmount: Double?,
            val userId: String?
        ) : Parcelable {
            @Parcelize
            data class Product(
                val description: String?,
                @SerializedName("_id")
                val id: String?,
                val image: List<Image?>?,
                val installationPincode: String?,
                val price: Int?,
                val rating: Int?,
                val priceToDisplay: Long?,
                val productId: String?,
                val productName: String?,
                @SerializedName("Productquantity")
                val productquantity: Int?,
                val isQuantityUnlimited: Int?,
                val quantity: Int?,
                val totalPrice: Int?
            ) : Parcelable {
                @Parcelize
                data class Image(
                    @SerializedName("_id")
                    val id: String?,
                    val url: String?
                ) : Parcelable
            }
        }
    }

}