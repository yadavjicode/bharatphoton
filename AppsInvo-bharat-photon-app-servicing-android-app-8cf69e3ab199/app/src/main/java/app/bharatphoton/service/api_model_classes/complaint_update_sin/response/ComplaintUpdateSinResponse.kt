package app.bharatphoton.service.api_model_classes.complaint_update_sin.response

import app.bharatphoton.service.api_model_classes.login.response.Count

data class ComplaintUpdateSinResponse(
    var code: Int?,
    var count: Count?,
    var `data`: ComplaintUpdateSinResponseData?,
    var message: String?
)
data class ComplaintUpdateSinResponseData(
    var __v: Int?,
    var _id: String?,
    var batteryVoltage: Double?,
    var complaintId: String?,
    var costSaved: Int?,
    var createdAt: String?,
    var customerId: String?,
    var customerName: String?,
    var flushTime: Int?,
    var image: List<ComplaintUpdateSinResponseImage?>?,
    var installationType: Int?,
    var isOtpVerfied: Boolean?,
    var is_deleted: Boolean?,
    var orderId: Any?,
    var otp: String?,
    var preflush: Int?,
    var productId: String?,
    var productImage: List<Any?>?,
    var range: Int?,
    var remark: String?,
    var staffId: String?,
    var status: Boolean?,
    var totalPayment: Int?,
    var updatedAt: String?,
    var userCount: Int?,
    var waterSaved: Int?
)
data class ComplaintUpdateSinResponseImage(
    var _id: String?,
    var url: String?
)