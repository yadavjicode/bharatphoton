package com.app.bharatphoton.module.productResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductListingForSearchResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<ProductListData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class ProductListData(
        val amazonProductLink: String?,
        val cartStatus: Int?,
        val categoryId: String?,
        val categoryName: String?,
        val createdAt: String?,
        val currencyType: String?,
        val description: String?,
        val discountPercentage: Int?,
        val features: List<Feature?>?,
        val flipcartProductLink: String?,
        @SerializedName("_id")
        val id: String?,
        val image: List<Image?>?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        @SerializedName("is_wishlist")
        var isWishlist: Int?,
        val price: Int?,
        val rating: Int?,
        val priceToDisplay: Int?,
        val productName: String?,
        val quantity: Int?,
        val replacement: Boolean?,
        val specification: List<Specification?>?,
        val subcategoryId: String?,
        val updatedAt: String?,
        val warrenty: Boolean?,
        val youtube: List<Youtube?>?
    ) : Parcelable {
        @Parcelize
        data class Feature(
            val description: String?,
            @SerializedName("_id")
            val id: String?,
            val label: String?
        ) : Parcelable

        @Parcelize
        data class Image(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable

        @Parcelize
        data class Specification(
            val filedName: String?,
            @SerializedName("_id")
            val id: String?,
            val value: String?
        ) : Parcelable

        @Parcelize
        data class Youtube(
            val demoVideo: String?,
            @SerializedName("_id")
            val id: String?,
            val installationVideo: String?
        ) : Parcelable
    }
}