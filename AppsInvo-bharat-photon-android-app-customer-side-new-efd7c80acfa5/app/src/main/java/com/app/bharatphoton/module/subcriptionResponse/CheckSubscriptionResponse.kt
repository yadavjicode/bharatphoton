package com.app.bharatphoton.module.subcriptionResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CheckSubscriptionResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    class Data : Parcelable
}