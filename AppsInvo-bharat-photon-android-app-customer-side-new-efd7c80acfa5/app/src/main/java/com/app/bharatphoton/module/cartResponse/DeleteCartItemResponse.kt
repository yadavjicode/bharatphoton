package com.app.bharatphoton.module.cartResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DeleteCartItemResponse(
    val code: Int?,
    val count: Count?,
    val data: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    class Data : Parcelable
}