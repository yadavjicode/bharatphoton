package com.app.bharatphoton.module.productResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDetailsResponse(
    val code: Int?, val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val productDetails: ProductDetails?,
        var relatedProducts: List<RelatedProduct?>?,
        val reviews: List<Review?>?,
        val subscriptionPrice: SubscriptionPrice?
    ) : Parcelable {
        @Parcelize
        data class ProductDetails(
            val amazonProductLink: String?,
            val cartStatus: Int?,
            val cartQuantity: Int?,
            val categoryId: String?,
            val title: String?,
            val createdAt: String?,
            val currencyType: String?,
            val description: String?,
            val discountPercentage: Int?,
            val isQuantityUnlimited: Int?,
            val features: List<Feature?>?,
            val finNo: String?,
            val flipcartProductLink: String?,
            @SerializedName("_id") val id: String?,
            val image: List<Image?>?,
            val installation: List<Installation?>?,
            val isActive: Boolean?,
            val isDeleted: Boolean?,
            @SerializedName("is_wishlist") var isWishlist: Int?,
            val modelNo: String?,
            val price: Double?,
            val priceToDisplay: Double?,
            val productName: String?,
            val categoryName: String?,
            val metaDescription: String?,
            val productSlug: String?,
            val quantity: Int?,
            val rating: Int?,
            val replacement: Boolean?,
            val specification: List<Specification?>?,
            val subcategoryId: String?,
            val updatedAt: String?,
            val uploadBrocheure: String?,
            val warrenty: Boolean?,
            val youtube: List<Youtube?>?
        ) : Parcelable {
            @Parcelize
            data class Feature(
                val description: String?,
                @SerializedName("_id") val id: String?,
                val label: String?
            ) : Parcelable

            @Parcelize
            data class Image(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable

            @Parcelize
            data class Installation(
                @SerializedName("_id") val id: String?, val pincode: Int?, val price: Int?
            ) : Parcelable

            @Parcelize
            data class Specification(
                val filedName: String?, @SerializedName("_id") val id: String?, val value: String?
            ) : Parcelable

            @Parcelize
            data class Youtube(
                val demoVideo: String?, @SerializedName("_id") val id: String?, val installationVideo: String?
            ) : Parcelable
        }

        @Parcelize
        data class Review(
            val createdAt: String?,
            @SerializedName("_id") val id: String?,
            val image: List<Image?>?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val productId: String?,
            val rating: Int?,
            val review: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val userId: String?,
            val userProfile: UserProfile?
        ) : Parcelable {
            @Parcelize
            data class UserProfile(
                val firstName: String?, @SerializedName("_id") val id: String?, val lastName: String?, val profileImage: String?
            ) : Parcelable
        }

        @Parcelize
        data class Image(
            @SerializedName("_id") val id: String?, val url: String?
        ) : Parcelable

        @Parcelize
        data class RelatedProduct(
            var categoryId: String?,
            var categoryName: String?,
            var currencyType: String?,
            @SerializedName("_id")
            var id: String?,
            var image: List<Image?>?,
            var modelNo: String?,
            var price: Int?,
            var priceToDisplay: Int?,
            var productName: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id")
                var id: String?,
                var url: String?
            ) : Parcelable
        }
        @Parcelize
        data class SubscriptionPrice(
            val currencyType: String?, val mode: String?, val planType: Int?, val price: Double?
        ) : Parcelable
    }
}