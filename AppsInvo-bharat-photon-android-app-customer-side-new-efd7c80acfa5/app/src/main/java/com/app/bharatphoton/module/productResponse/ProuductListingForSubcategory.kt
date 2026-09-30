package com.app.bharatphoton.module.productResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductListingForSubcategory(
    val code: Int?,
    val count: Int?,
    val `data`: ProductListForSubscriptionData?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class ProductListForSubscriptionData(
        val subcategoriesList: List<Subcategories?>?,
        val subcategorydescription: String?,
        val subcategoryname: String?
    ) : Parcelable {
        @Parcelize
        data class Subcategories(
            val cartStatus: Int?,
            val categoryId: String?,
            val categoryName: String?,
            val createdAt: String?,
            val currencyType: String?,
            val discountPercentage: Int?,
            @SerializedName("_id")
            val id: String?,
            val image: List<Image?>?,
            @SerializedName("is_wishlist")
            var isWishlist: Int?,
            val price: Double?,
            val priceToDisplay: Double?,
            val productName: String?,
            val quantity: Int?,
            val isQuantityUnlimited: Int?,
            val cartQuantity: Int?,
            val subcategoryId: String?
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