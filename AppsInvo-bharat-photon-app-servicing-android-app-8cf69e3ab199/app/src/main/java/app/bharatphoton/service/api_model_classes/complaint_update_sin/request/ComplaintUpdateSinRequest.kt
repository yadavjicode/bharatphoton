package app.bharatphoton.service.api_model_classes.complaint_update_sin.request

data class ComplaintUpdateSinRequest(
    var batteryVoltage: Double?,
    var complaintId: String?,
    var costSaved: String?,
    var customerId: String?,
    var customerName: String?,
    var flushTime: String?,
    var id: String?,
    var image: List<ComplaintUpdateSinRequestImage>,
    var preflush: String?,
    var productId: String?,
    var range: String?,
    var remark: String?,
    var totalPayment: String?,
    var userCount: String?,
    var waterSaved: String?,
    var finNo: String?,
    var sin: String?,
    var resolveDate: String?,
    var resolveAction: String?,
    var offDelay: String?,
    var cutOffDelay: String?,
)
data class ComplaintUpdateSinRequestImage(
    var _id: String?,
    var url: String?
)