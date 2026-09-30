package com.app.bharatphoton.module.reviewResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddReviewResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val image: List<Image?>?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val productId: String?,
        val rating: Int?,
        val review: String?,
        val status: Boolean?,
        val updatedAt: String?,
        val userId: String?
    ) : Parcelable {
        @Parcelize
        data class Image(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}