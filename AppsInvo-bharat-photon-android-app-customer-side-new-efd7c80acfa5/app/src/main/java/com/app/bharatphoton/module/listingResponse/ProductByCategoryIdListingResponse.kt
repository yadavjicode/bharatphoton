package com.app.bharatphoton.module.listingResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductByCategoryIdListingResponse(
    var code: Int?,
    var count: Int?,
    var `data`: List<ProductList?>?,
    var message: String?
) : Parcelable {
    @Parcelize
    data class ProductList(
        var categoryId: String?,
        @SerializedName("_id")
        var id: String?,
        var modelNo: String?,
        var productName: String?,
        var title: String?
    ) : Parcelable
}