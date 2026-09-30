package com.app.bharatphoton.module.notificationRessponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class NotificationCountResponse(
    val code: Int?,
    val count: Int?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Data : Parcelable
}