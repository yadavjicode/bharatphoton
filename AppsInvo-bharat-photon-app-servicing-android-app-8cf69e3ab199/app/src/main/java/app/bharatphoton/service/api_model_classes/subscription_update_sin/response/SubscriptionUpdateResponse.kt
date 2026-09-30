package app.bharatphoton.service.api_model_classes.subscription_update_sin.response

import app.bharatphoton.service.api_model_classes.login.response.Count
import app.bharatphoton.service.api_model_classes.subscription_update_sin.request.SubscriptionUpdateRequestImage

data class SubscriptionUpdateResponse(
    var code: Int?,
    var count: Count?,
    var `data`: SubscriptionUpdateResponseData?,
    var message: String?
)
data class SubscriptionUpdateResponseData(
    var __v: Int?,
    var _id: String?,
    var batteryVoltage: Double?,
    var categoryName: String?,
    var complaintId: Any?,
    var costSaved: Int?,
    var createdAt: String?,
    var customerId: String?,
    var customerName: String?,
    var flushTime: Int?,
    var image: List<SubscriptionUpdateRequestImage?>?,
    var installationType: Int?,
    var isOtpVerfied: Boolean?,
    var is_deleted: Boolean?,
    var modelNo: String?,
    var orderId: String?,
    var otp: String?,
    var preflush: Int?,
    var productDescription: String?,
    var productId: String?,
    var productImage: List<SubscriptionUpdateRequestImage?>?,
    var productName: String?,
    var range: Int?,
    var remark: String?,
    var sin: String?,
    var staffId: String?,
    var status: Boolean?,
    var subscriptionId: String?,
    var totalPayment: Int?,
    var updatedAt: String?,
    var updatedByAdmin: Int?,
    var userCount: Int?,
    var waterSaved: Int?
)