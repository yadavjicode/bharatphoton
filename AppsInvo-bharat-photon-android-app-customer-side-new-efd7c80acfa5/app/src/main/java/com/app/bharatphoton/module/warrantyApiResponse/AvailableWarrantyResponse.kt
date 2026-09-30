package com.app.bharatphoton.module.warrantyApiResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AvailableWarrantyResponse(
    val code: Int?,
    val count: Int?,
    val data: List<AvailableWarrantyData?>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class AvailableWarrantyData(
        val categoryId: String?,
        val createdAt: String?,
        val currencyType: String?,
        val description: String?,
        @SerializedName("_id")
        val id: String?,
        val image: String?,
        @SerializedName("is_deleted")
        val isDeleted: Boolean?,
        val mode: String?,
        val price: Int?,
        val products: List<String?>?,
        val status: Boolean?,
        val title: String?,
        val updatedAt: String?
    ) : Parcelable
}