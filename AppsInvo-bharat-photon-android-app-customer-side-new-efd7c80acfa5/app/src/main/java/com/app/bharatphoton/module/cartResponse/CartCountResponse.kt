package com.app.bharatphoton.module.cartResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CartCountResponse(
    val code: Int?,
    val count: Int?,
    val data: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Data : Parcelable
}