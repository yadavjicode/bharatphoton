package app.bharatphoton.service.api_model_classes.update_sin.response

import app.bharatphoton.service.api_model_classes.login.response.Count

data class UpdateSinResponse(
    var code: Int?,
    var count: Count?,
    var `data`: UpdateSinResponseData?,
    var message: String?
)
data class UpdateSinResponseData(
    var __v: String?,
    var _id: String?,
    var batteryVoltage: String?,
    var createdAt: String?,
    var customerName: String?,
    var flushTime: String?,
    var isOtpVerfied: Boolean?,
    var is_deleted: Boolean?,
    var orderId: String?,
    var otp: String?,
    var preflush: String?,
    var productId: String?,
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
    var warrentyStartDate: String?
)