package com.app.bharatphoton.module.listingResponse


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CityListResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<String?>?,
    val message: String?
) : Parcelable