package app.bharatphoton.service.api_model_classes

data class SubscriptionInstallationListResponse(
    var code: Int?,
    var count: Int?,
    var `data`: List<SubscriptionInstallationListResponseData>?,
    var message: String?
)
data class SubscriptionInstallationListResponseData(
    var _id: String?,
    var uuid: String?,
    var userId: String?,
    var subscriptionId: String?,
    var productId: String?,
    var quantity: Int?,
    var productImage: String?,
    var productName: String?,
    var categoryName: String?,
    var modelNo: String?,
    var planType: Int?,
    var price: Int?,
    var mode: String?,
    var currencyType: String?,
    var staffId: String?,
    var finalOrderStatus: Int?,
    var resolveDate: String?,
    var firstName: String?,
    var lastName: String?,
    var profileImage: String?,
    var phoneNo: Long?,
    var country_code: String?,

    val companyName: String?,
    val businessEmail: String?,
    val businessPhoneNo: String?,
)