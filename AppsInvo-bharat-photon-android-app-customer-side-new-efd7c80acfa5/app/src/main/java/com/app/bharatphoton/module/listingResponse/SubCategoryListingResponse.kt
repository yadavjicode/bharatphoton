package com.app.bharatphoton.module.listingResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SubCategoryListingResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<SubCategoryData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class SubCategoryData(
        val categoryId: String?,
        val categoryName: String?,
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val image: String?,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val subcategoryName: String?,
        val updatedAt: String?
    ) : Parcelable
}