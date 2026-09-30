package app.bharatphoton.service.api_model_classes

import android.os.Parcelable
import app.bharatphoton.service.api_model_classes.ProductDetailResponse.ProductDetailResponseData.ProductDetailResponseAddressDetails
import app.bharatphoton.service.api_model_classes.login.response.Count
import kotlinx.parcelize.Parcelize

data class SubscriptionDetailsResponse(
    var code: Int?, var count: Count?, var `data`: SubscriptionDetailsResponseData?, var message: String?
)

@Parcelize
data class SubscriptionDetailsResponseData(
    var addressDetails: SubscriptionDetailsResponseAddressDetails?,
    var subscriptionList: SubscriptionDetailsResponseSubscriptionList?,
    var userDetails: SubscriptionDetailsResponseUserDetails?,
    var installationDetails: List<ProductDetailInstallationDetailsResponse>?,
) : Parcelable

@Parcelize
data class SubscriptionDetailsResponseAddressDetails(
    var ZipCode: Int?, var _id: String?, var address: String?, var city: String?, var createdAt: String?, var image: List<ProductDetailResponseAddressDetails.ProductDetailResponseAddressDetailsImage>?, var is_deleted: Boolean?, var orderId: String?, var state: String?, var status: Boolean?, var updatedAt: String?, var userId: String?
) : Parcelable

@Parcelize
data class SubscriptionDetailsResponseSubscriptionList(
    var ZipCode: String?,
    var _id: String?,
    var address: String?,
    var addressId: String?,
    var address_type: String?,
    var amountInPaisa: String?,
    var categoryName: String?,
    var city: String?,
    var countryCode: String?,
    var createdAt: String?,
    var currencyType: String?,
    var finalOrderStatus: String?,
    var finalTotalAmount: Double?,
    var firstName: String?,
    var installationStatus: String?,
    var invoiceUrl: String?,
    var is_deleted: Boolean?,
    var lastName: String?,
    var mode: String?,
    var modelNo: String?,
    var paymentMethod: String?,
    var paymentMode: String?,
    var paymentStatus: String?,
    var phoneNo: String?,
    var planType: String?,
    var price: String?,
    var productId: String?,
    var productImage: String?,
    var productName: String?,
    var productDescription: String?,
    var quantity: String?,
    var razorpayOrderId: String?,
    var finNo: String?,
    var resolveDate: String?,
    var sin: String?,
    var staffId: String?,
    var state: String?,
    var status: Boolean?,
    var subscriptionExpiryDate: String?,
    var subscriptionId: String?,
    var subscriptionStartDate: String?,
    var taxAmount: Double?,
    var totalPrice: String?,
    var updatedAt: String?,
    var userId: String?,
    var isFieldAvailable: String?,
    val isUrinalSensorAvilable: String?,
    var uuid: String?
) : Parcelable

@Parcelize
data class SubscriptionDetailsResponseUserDetails(
    var businessEmail: String?, var businessPhoneNo: String?, var companyName: String?, var country_code: String?, var email: String?, var firstName: String?, var lastName: String?, var phoneNo: String?, var profileImage: String?
) : Parcelable

@Parcelize
data class ProductDetailInstallationDetailsResponse(
    val _id: String?,
    val customerName: String?,
    val remark: String?,
    val finNo: String?,
    val installationStatus: Int?,
    val image: List<ProductDetailResponseAddressDetails.ProductDetailResponseAddressDetailsImage>?,
) : Parcelable