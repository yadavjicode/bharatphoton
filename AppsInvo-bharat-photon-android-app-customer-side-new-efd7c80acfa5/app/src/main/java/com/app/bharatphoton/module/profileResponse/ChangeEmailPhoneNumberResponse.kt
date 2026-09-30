package com.app.bharatphoton.module.profileResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ChangeEmailPhoneNumberResponse(
    val code: Int?,
    val count: Count?,
    val `data`: Data?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        @SerializedName("auth_token")
        val authToken: String?
    ) : Parcelable
}