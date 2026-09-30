package com.app.bharatphoton.module.listingResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CategoryListingResponse(
    val code: Int?,
    val count: Int?,
    val data: List<CategoryListData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class CategoryListData(
        val categoryName: String?,
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val image: String?,
        var isSelected: Boolean,
        val isActive: Boolean?,
        val isDeleted: Boolean?,
        val updatedAt: String?
    ) : Parcelable
}