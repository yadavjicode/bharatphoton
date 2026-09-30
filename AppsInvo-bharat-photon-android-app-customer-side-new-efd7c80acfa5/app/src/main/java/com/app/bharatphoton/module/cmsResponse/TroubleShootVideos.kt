package com.app.bharatphoton.module.cmsResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TroubleShootVideos(
    val code: Int?,
    val count: Int?,
    val `data`: TroubleShootVideoData?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class TroubleShootVideoData(
        val categoryId: String?,
        val createdAt: String?,
        @SerializedName("_id")
        val id: String?,
        val installationVideo: List<InstallationVideo?>?,
        @SerializedName("is_status")
        val isStatus: Int?,
        val products: List<String?>?,
        val troubleVideos: List<TroubleVideo?>?,
        val type: String?,
        val updatedAt: String?,
        val userManual: String?,
        val userType: Int?
    ) : Parcelable {
        @Parcelize
        data class InstallationVideo(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable

        @Parcelize
        data class TroubleVideo(
            @SerializedName("_id")
            val id: String?,
            val url: String?
        ) : Parcelable
    }
}