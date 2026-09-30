package com.app.bharatphoton.module.homePageResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomePageResponse(
    val code: Int?, val count: Int?, val data: HomePageData?, val message: String?
) : Parcelable {
    @Parcelize
    data class HomePageData(
        val banner: List<Banner?>?,
        val blog: List<Blog?>?,
        val categories: List<Category?>?,
        val cms: Cms?,
        val news: List<New?>?,
        val costSavedData: CostSavedData?,
        val products: List<Product?>?
    ) : Parcelable {
        @Parcelize
        data class Banner(
            val appImage: String?,
            val createdAt: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val link: String?,
            val status: Boolean?,
            val updatedAt: String?,
            val web: String?
        ) : Parcelable

        @Parcelize
        data class CostSavedData(
            val totalCostSaved: Double?,
            val totalWaterSaved: Int?
        ) : Parcelable


        @Parcelize
        data class Blog(
            val createdAt: String?,
            val descriptions: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            @SerializedName("is_Active") val isActive: Boolean?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val title: String?,
            val updatedAt: String?
        ) : Parcelable

        @Parcelize
        data class Category(
            val categoryName: String?,
            val createdAt: String?,
            val description: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            val isActive: Boolean?,
            val isDeleted: Boolean?,
            val updatedAt: String?
        ) : Parcelable

        @Parcelize
        data class Cms(
            val createdAt: String?,
            val descriptions: String?,
            @SerializedName("_id") val id: String?,
            @SerializedName("is_status") val isStatus: Int?,
            val title: String?,
            val type: String?,
            val updatedAt: String?,
            val userType: Int?
        ) : Parcelable

        @Parcelize
        data class New(
            val createdAt: String?,
            val descriptions: String?,
            @SerializedName("_id") val id: String?,
            val image: String?,
            @SerializedName("is_Active") val isActive: Boolean?,
            @SerializedName("is_deleted") val isDeleted: Boolean?,
            val title: String?,
            val updatedAt: String?
        ) : Parcelable

        @Parcelize
        data class Product(
            val cartStatus: Int?,
            val categoryId: String?,
            val categoryName: String?,
            val currencyType: String?,
            val discountPercentage: Int?,
            @SerializedName("_id") val id: String?,
            val image: List<Image?>?,
            @SerializedName("is_wishlisted") val isWishlisted: Int?,
            val price: Int?,
            val priceToDisplay: Int?,
            val productName: String?,
            val subcategoryId: String?,
            val subcategoryName: String?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id") val id: String?, val url: String?
            ) : Parcelable
        }
    }
}