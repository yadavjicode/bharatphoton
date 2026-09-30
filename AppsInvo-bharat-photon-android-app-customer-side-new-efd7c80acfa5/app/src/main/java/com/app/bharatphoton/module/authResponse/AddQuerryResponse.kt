package com.app.bharatphoton.module.authResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddQuerryResponse(
    var code: Int?,
    var count: Count?,
    var `data`: Data?,
    var message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        var categoryId: String?,
        var countryCode: String?,
        var createdAt: String?,
        var email: String?,
        var fullName: String?,
        @SerializedName("_id")
        var id: String?,
        @SerializedName("is_deleted")
        var isDeleted: Boolean?,
        @SerializedName("is_status")
        var isStatus: Boolean?,
        var phoneNo: Long?,
        var updatedAt: String?
    ) : Parcelable
}