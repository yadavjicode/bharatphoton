package app.bharatphoton.service.api_model_classes.subscription_update_sin.request

data class SubscriptionUpdateRequest(
    var year: String?,
    var month: String?,
    var batteryVoltage: Double?,
    var costSaved: String?,
    var customerName: String?,
    var flushTime: String?,
    var id: String?,
    var image: List<SubscriptionUpdateRequestImage>,
    var orderId: String?,
    var preflush: String?,
    var productId: String?,
    var range: String?,
    var remark: String?,
    var sin: String?,
    var finNo: String?,
    var subscriptionId: String?,
    var totalPayment: String?,
    var userCount: String?,
    var waterSaved: String?,
    var offDelay: String?,
    var cutOffDelay: String?,
)
data class SubscriptionUpdateRequestImage(
    var _id: String?,
    var url: String?
)