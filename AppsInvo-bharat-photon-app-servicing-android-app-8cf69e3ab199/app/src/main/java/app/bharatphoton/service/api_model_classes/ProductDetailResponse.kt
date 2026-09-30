package app.bharatphoton.service.api_model_classes


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import app.bharatphoton.service.api_model_classes.ProductDetailResponse.ProductDetailResponseData.ProductDetailResponseAddressDetails.ProductDetailResponseAddressDetailsImage

@Parcelize
data class ProductDetailResponse(
    val code: Int?,
    val count: Count?,
    val `data`: ProductDetailResponseData?,
    val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class ProductDetailResponseData(
        val addressDetails: ProductDetailResponseAddressDetails?,
        val categoryName: String?,
        @SerializedName("_id")
        val id: String?,
        val installationsData: List<ProductDetailResponseInstallationDetails>,
        val isFieldAvailable: String?,
        val isUrinalSensorAvilable: String?,
        val product: ProductDetailResponseProduct?,
        val userDetails: UserDetails?,
        val userId: String?,
        val uuid: String?
    ) : Parcelable {
        @Parcelize
        data class ProductDetailResponseAddressDetails(
            val address: String?,
            val city: String?,
            @SerializedName("_id")
            val id: String?,
            val image: List<ProductDetailResponseAddressDetailsImage>?,
            val orderId: String?,
            val state: String?,
            @SerializedName("ZipCode")
            val zipCode: String?
        ) : Parcelable {
            @Parcelize
            class ProductDetailResponseAddressDetailsImage (
                val id: String?,
                val url: String?
            ) : Parcelable
        }
        @Parcelize
        class ProductDetailResponseInstallationDetails(
            @SerializedName("_id")
            val id: String?,
            val customerName: String?,
            val remark: String?,
            val finNo: String?,
            val installationStatus: Int?,
            val image: List<ProductDetailResponseAddressDetailsImage>?,
        ) : Parcelable
        @Parcelize
        data class ProductDetailResponseProduct(
            val assignDate: String?,
            val cancelledDate: String?,
            val categoryId: String?,
            val deliveredProductDate: String?,
            val description: String?,
            val dispatchedProductDate: String?,
            @SerializedName("_id")
            val id: String?,
            val image: String?,
            val installationDetails: List<InstallationDetail>,
            val installationPincode: String?,
            val installationPrice: String?,
            val installationStatus: String?,
            val modelNo: String?,
            val orderStatus: String?,
            val pendingProductDate: String?,
            val price: String?,
            val processedProductDate: String?,
            val productId: String?,
            @SerializedName("product_name")
            val productName: String?,
            val quantity: String?,
            val resolveDate: String?,
            val staffId: String?,
            val totalPrice: String?,
            val warrentyEndDate: String?,
            val warrentyStartDate: String?
        ) : Parcelable {
            @Parcelize
            data class InstallationDetail(
                val finNo: String?,
                @SerializedName("_id")
                val id: String?,
                val installationStatus: String?
            ) : Parcelable
        }
        @Parcelize
        data class UserDetails(
            @SerializedName("country_code")
            val countryCode: String?,
            val email: String?,
            val firstName: String?,
            val lastName: String?,
            val phoneNo: Long?,
            val profileImage: String?
        ) : Parcelable
    }
}