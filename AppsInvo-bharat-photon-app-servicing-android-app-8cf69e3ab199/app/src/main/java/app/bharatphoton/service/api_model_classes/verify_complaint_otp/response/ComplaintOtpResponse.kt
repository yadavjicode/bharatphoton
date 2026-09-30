package app.bharatphoton.service.api_model_classes.verify_complaint_otp.response

import app.bharatphoton.service.api_model_classes.login.response.Count

data class ComplaintOtpResponse(
    var code: Int?,
    var count: Count?,
    var `data`: ComplaintOtpResponseData?,
    var message: String?
)
data class ComplaintOtpResponseData(
    var __v: String?,
    var _id: String?,
    var batteryVoltage: String?,
    var complaintId: String?,
    var costSaved: String?,
    var createdAt: String?,
    var customerId: String?,
    var customerName: String?,
    var flushTime: String?,
    var installationType: String?,
    var isOtpVerfied: Boolean?,
    var is_deleted: Boolean?,
    var orderId: String?,
    var otp: String?,
    var preflush: String?,
    var productId: String?,
    var range: String?,
    var remark: String?,
    var staffId: String?,
    var status: Boolean?,
    var totalPayment: String?,
    var updatedAt: String?,
    var userCount: String?,
    var waterSaved: String?
)