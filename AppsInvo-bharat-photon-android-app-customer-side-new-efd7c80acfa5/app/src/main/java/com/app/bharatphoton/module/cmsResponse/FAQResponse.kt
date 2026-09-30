package com.app.bharatphoton.module.cmsResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FAQResponse(
    val code: Int?, val count: Int?, val `data`: List<FAQData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class FAQData(
        val answer: String?,
        val createdAt: String?,
        @SerializedName("_id") val id: String?,
        @SerializedName("is_deleted") val isDeleted: Int?,
        @SerializedName("is_status") val isStatus: Int?,
        val question: String?,
        val updatedAt: String?
    ) : Parcelable
}