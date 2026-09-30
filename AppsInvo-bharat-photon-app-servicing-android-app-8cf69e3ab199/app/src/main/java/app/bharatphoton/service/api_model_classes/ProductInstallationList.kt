package app.bharatphoton.service.api_model_classes


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductInstallationList(
    val code: Int?,
    val count: Int?,
    val `data`: List<ProductInstallationListData>?,
    val message: String?
) : Parcelable {
    @Parcelize
    data class ProductInstallationListData(
        val createdAt: String?,
        val orderId: String?,
        val product: ProductInstallationListProduct?,
        val userDetails: ProductInstallationListUserDetails?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class ProductInstallationListProduct(
            val assignDate: String?,
            val cancelledDate: String?,
            val categoryId: String?,
            val deliveredProductDate: String?,
            val description: String?,
            val dispatchedProductDate: String?,
            @SerializedName("_id")
            val id: String?,
            val image: String?,
            val installationDetails: List<ProductInstallationListInstallationDetail?>?,
            val installationPincode: Int?,
            val installationPrice: Int?,
            val installationStatus: Int?,
            val modelNo: String?,
            val orderStatus: Int?,
            val pendingProductDate: String?,
            val price: Int?,
            val processedProductDate: String?,
            val productId: String?,
            @SerializedName("product_name")
            val productName: String?,
            val quantity: Int?,
            val resolveDate: String?,
            val staffId: String?,
            val totalPrice: Int?,
            val warrentyEndDate: String?,
            val warrentyMonth: String?,
            val warrentyStartDate: String?
        ) : Parcelable {
            @Parcelize
            data class ProductInstallationListInstallationDetail(
                val finNo: String?,
                @SerializedName("_id")
                val id: String?,
                val installationStatus: Int?
            ) : Parcelable
        }

        @Parcelize
        data class ProductInstallationListUserDetails(
            @SerializedName("country_code")
            val countryCode: String?,
            val email: String?,
            val firstName: String?,
            @SerializedName("_id")
            val id: String?,
            val lastName: String?,
            val phoneNo: Long?,
            val profileImage: String?,

            val companyName: String?,
            val businessEmail: String?,
            val businessPhoneNo: String?,
        ) : Parcelable
    }
}