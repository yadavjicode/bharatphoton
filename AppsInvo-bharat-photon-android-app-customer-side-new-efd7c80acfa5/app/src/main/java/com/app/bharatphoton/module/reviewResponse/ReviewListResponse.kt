package com.app.bharatphoton.module.reviewResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewListResponse(
    val code: Int?, val count: Int?, val `data`: List<ReviewListData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class ReviewListData(
        val createdAt: String?,
        @SerializedName("_id") val id: String?,
        val image: List<Image?>?,
        val isApprovedByAdmin: Boolean?,
        @SerializedName("is_deleted") val isDeleted: Boolean?,
        val productId: String?,
        val rating: Double?,
        val review: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?,
        val userFirstName: String?,
        val userLastName: String?,
        val userProfileImage: String?,
    ) : Parcelable {
        @Parcelize
        data class Image(
            @SerializedName("_id") val id: String?, val url: String?
        ) : Parcelable
    }
}