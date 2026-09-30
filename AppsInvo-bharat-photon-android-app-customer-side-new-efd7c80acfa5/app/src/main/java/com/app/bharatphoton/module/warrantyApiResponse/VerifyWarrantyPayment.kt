package com.app.bharatphoton.module.warrantyApiResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class VerifyWarrantyPayment(
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