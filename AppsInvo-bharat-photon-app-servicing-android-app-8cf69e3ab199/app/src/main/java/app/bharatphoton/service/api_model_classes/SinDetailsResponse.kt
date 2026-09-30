package app.bharatphoton.service.api_model_classes

data class SinDetailsResponse(
    var code: Int?,
    var count: Int?,
    var `data`: SinDetailsResponseData?,
    var message: String?
)
data class SinDetailsResponseData(
    var _id: String?,
    var batteryVoltage: String?,
    var createdAt: String?,
    var customerId: String?,
    var customerName: String?,
    var finNo: String?,
    var waterSaved: String?,
    var costSaved: String?,
    var flushTime: String?,
    var offDelay: String?,
    var cutOffDelay: String?,
    var image: List<SinDetailsResponseProductImage?>?,
    var isOtpVerfied: Boolean?,
    var is_deleted: Boolean?,
    var modelNo: String?,
    var orderId: String?,
    var subscriptionId: String?,
    var otp: String?,
    var preflush: String?,
    var productId: String?,
    var productImage: List<SinDetailsResponseProductImage?>?,
    var productName: String?,
    var range: String?,
    var remark: String?,
    var sin: String?,
    var staffId: String?,
    var status: Boolean?,
    var totalPayment: String?,
    var updatedAt: String?,
    var userCount: String?,
    var warrentyEndDate: String?,
    var warrentyMonth: String?,
    var month: String?,
    var year: Int?,
    var warrentyStartDate: String?
)
data class SinDetailsResponseProductImage(
    var _id: String?,
    var url: String?
)