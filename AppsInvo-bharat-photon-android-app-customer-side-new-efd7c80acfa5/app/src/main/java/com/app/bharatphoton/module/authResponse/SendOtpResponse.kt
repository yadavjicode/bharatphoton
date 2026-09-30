package com.app.bharatphoton.module.authResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SendOtpResponse(
    var code: Int?,
    var count: Count?,
    var `data`: String?,
    var message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable
}