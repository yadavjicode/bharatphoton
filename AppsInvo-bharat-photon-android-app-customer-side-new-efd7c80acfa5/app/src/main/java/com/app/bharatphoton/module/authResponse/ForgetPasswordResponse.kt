package com.app.bharatphoton.module.authResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ForgetPasswordResponse(
    val code: Int?,
    val count: Count?,
    val `data`: String?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable
}