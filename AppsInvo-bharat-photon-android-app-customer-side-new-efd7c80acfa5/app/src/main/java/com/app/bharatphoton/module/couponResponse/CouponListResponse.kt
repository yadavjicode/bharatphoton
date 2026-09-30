package com.app.bharatphoton.module.couponResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CouponListResponse(
    val code: Int?, val count: Int?, val `data`: List<CouponListData?>?, val message: String?
) : Parcelable {
    @Parcelize
    data class CouponListData(
        val couponCode: String?,
        val description: String?,
        val discount: Int?,
        val endDate: String?,
        val isCouponApplied: Boolean?,
        val couponType: Int?,
        @SerializedName("_id") val id: String?,
        val productId: String?,
        val startDate: String?,
        val title: String?
    ) : Parcelable
}