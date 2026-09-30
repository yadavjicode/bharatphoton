package com.app.bharatphoton.module.wishlistResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class WishListResponse(
    val code: Int?,
    val count: Int?,
    val data: List<WishListData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class WishListData(
        val currencyType: String?,
        @SerializedName("_id")
        val id: String?,
        val productId: String?,
        val price: Int?,
        val priceToDisplay: Long?,
        val productImage: List<ProductImage?>?,
        val productName: String?,
        val rating: Int?

    ) : Parcelable {
        @Parcelize
        data class ProductImage(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}