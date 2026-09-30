package com.app.bharatphoton.module.orderResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReorderResponse(
    val code: Int?, val count: Count?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val cartDetails: CartDetails?, val unavilableProducts: List<UnavilableProduct?>?
    ) : Parcelable {
        @Parcelize
        data class CartDetails(
            val addressId: String?,
            val createdAt: String?,
            val deviceId: String?,
            @SerializedName("_id") val id: String?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val products: List<Product?>?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?,
            @SerializedName("__v") val v: Int?
        ) : Parcelable {
            @Parcelize
            data class Product(
                val coupons: List<String?>?, @SerializedName("_id") val id: String?, val installationPincode: String?, val productId: String?, val quantity: Int?
            ) : Parcelable
        }

        @Parcelize
        data class UnavilableProduct(
            val description: String?, @SerializedName("_id") val id: String?, val image: List<Image?>?, val price: Int?, val priceToDisplay: Int?, val productName: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable
        }
    }
}