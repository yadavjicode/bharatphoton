package app.bharatphoton.service.api_model_classes.verify_complaint_otp.request

data class ComplaintOtpRequest(
    var complaintId: String?,
    var id: String?,
    var otp: String?
)