package com.app.bharatphoton.module.authResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class S3Response(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val accessKeyId: String?,
        val bucketName: String?,
        val region: String?,
        val secretAccessKey: String?
    ) : Parcelable
}