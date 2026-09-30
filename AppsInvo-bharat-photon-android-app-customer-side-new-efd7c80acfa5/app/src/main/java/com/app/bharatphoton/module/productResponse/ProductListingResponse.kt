package com.app.bharatphoton.module.productResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductListingResponse(
    val code: Int?, val count: Int?, val data: ProductListData?, val message: String?
) : Parcelable {
    @Parcelize
    data class ProductListData(
        val categoryName: String?, val description: String?, val subcategoryListing: List<SubcategoryListing?>?
    ) : Parcelable {
        @Parcelize
        data class SubcategoryListing(
            @SerializedName("_id") val id: String?, val products: List<ProductList?>?, val subcategoryName: String?
        ) : Parcelable {
            @Parcelize
            data class ProductList(
                val cartStatus: Int?,
                val categoryId: String?,
                val categoryName: String?,
                val cartQuantity: Int?,
                val rating: Int?,
                val currencyType: String?,
                val discountPercentage: Int?,
                var quantity: Int?,
                var isQuantityUnlimited: Int?,
                @SerializedName("_id") val id: String?,
                val image: List<Image?>?,
                @SerializedName("is_wishlist") var isWishlist: Int?,
                val price: Double?,
                val priceToDisplay: Double?,
                val productName: String?,
                val subcategoryId: String?
            ) : Parcelable {
                @Parcelize
                data class Image(
                    @SerializedName("_id") val id: String?, val url: String?
                ) : Parcelable
            }
        }
    }
}