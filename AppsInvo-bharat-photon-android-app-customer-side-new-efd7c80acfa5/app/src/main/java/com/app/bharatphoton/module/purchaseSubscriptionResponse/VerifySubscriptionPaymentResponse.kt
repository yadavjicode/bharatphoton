package com.app.bharatphoton.module.purchaseSubscriptionResponse


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifySubscriptionPaymentResponse(
    val code: Int?, val count: Count?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    class Data : Parcelable
}