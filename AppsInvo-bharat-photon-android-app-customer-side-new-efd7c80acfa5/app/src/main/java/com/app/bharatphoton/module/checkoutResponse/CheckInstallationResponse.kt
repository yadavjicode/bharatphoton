package com.app.bharatphoton.module.checkoutResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CheckInstallationResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val cart: Cart?
    ) : Parcelable {
        @Parcelize
        data class Cart(
            @SerializedName("_id")
            val id: String?,
            val products: List<Product?>?
        ) : Parcelable {
            @Parcelize
            data class Product(
                @SerializedName("_id")
                val id: String?,
                val installation: Int?,
                val installationPincode: Int?,
                val productId: String?,
                val quantity: Int?
            ) : Parcelable
        }
    }
}