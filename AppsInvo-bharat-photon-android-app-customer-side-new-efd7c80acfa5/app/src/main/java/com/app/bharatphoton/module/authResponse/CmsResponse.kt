package com.app.bharatphoton.module.authResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CmsResponse(
    val code: Int?,
    val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val createdAt: String?,
        val descriptions: String?,
        @SerializedName("_id")
        val id: String?,
        @SerializedName("is_status")
        val isStatus: Int?,
        val title: String?,
        val type: String?,
        val updatedAt: String?,
        val userType: Int?
    ) : Parcelable
}