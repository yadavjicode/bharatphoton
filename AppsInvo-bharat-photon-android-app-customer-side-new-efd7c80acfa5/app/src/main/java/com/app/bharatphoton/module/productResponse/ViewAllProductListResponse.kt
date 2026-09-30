package com.app.bharatphoton.module.productResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ViewAllProductListResponse(
    var code: Int?,
    var count: Int?,
    var `data`: ViewAllProductListData?,
    var message: String?
) : Parcelable {
    @Parcelize
    data class ViewAllProductListData(
        var name: String?,
        var productList: List<Product?>?,
        var subcategorydescription: String?
    ) : Parcelable {
        @Parcelize
        data class Product(
            var cartQuantity: Int?,
            var cartStatus: Int?,
            var categoryId: String?,
            var categoryName: String?,
            var createdAt: String?,
            var currencyType: String?,
            var discountPercentage: Int?,
            @SerializedName("_id")
            var id: String?,
            var image: List<Image?>?,
            var isQuantityUnlimited: Int?,
            @SerializedName("is_wishlist")
            var isWishlist: Int?,
            var price: Double?,
            var priceToDisplay: Double?,
            var productName: String?,
            var quantity: Int?,
            var rating: Int?,
            var subcategoryId: String?,
            var warrentyMonth: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id")
                var id: String?,
                var url: String?
            ) : Parcelable
        }
    }
}