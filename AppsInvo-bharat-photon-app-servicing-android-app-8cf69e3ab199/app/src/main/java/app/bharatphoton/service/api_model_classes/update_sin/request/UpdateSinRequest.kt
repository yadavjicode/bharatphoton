package app.bharatphoton.service.api_model_classes.update_sin.request

data class UpdateSinRequest(
    var id: String?,
    var month: String?,
    var batteryVoltage: String?,
    var customerName: String?,
    var waterSaved: String?,
    var costSaved: String?,
    var flushTime: String?,
    var image: ArrayList<UpdateSinRequestImage>?,
    var orderId: String?,
    var preflush: String?,
    var productId: String?,
    var range: String?,
    var remark: String?,
    var sin: String?,
    var finNo: String?,
    var totalPayment: String?,
    var userCount: String?,
    var warrentyEndDate: String?,
    var year: String?,
    var warrentyMonth: String?,
    var warrentyStartDate: String?,
    var offDelay: String?,
    var cutOffDelay: String?,
)
data class UpdateSinRequestImage(
    var _id: String?,
    var url: String?
)