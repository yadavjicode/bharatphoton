package app.bharatphoton.service.api_model_classes


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductComplaintListingResponse(
    val code: Int?,
    val count: Int?,
    val `data`: List<ProductComplaintListingResponseData>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class ProductComplaintListingResponseData(
        val complaintReason: String?,
        val description: String?,
        val complaintStatus: Int?,
        @SerializedName("country_code")
        val countryCode: String?,
        val createdAt: String?,
        val firstName: String?,
        @SerializedName("_id")
        val id: String?,
        val lastName: String?,
        val companyName: String?,
        val phoneNo: Long?,
        val profileImage: String?,
        val resolveDate: String?,
        val sin: String?,
        val updatedAt: String?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable
}