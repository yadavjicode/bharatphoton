package com.app.bharatphoton.module.wishlistResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DeleteWishListResponse(
    val code: Int?,
    val count: Count?,
    val data: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val idDeleted: Boolean?,
        val isActive: Boolean?,
        val productId: String?,
        val updatedAt: String?,
        val userId: String?
    ) : Parcelable
}