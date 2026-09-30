package app.bharatphoton.service.api_model_classes.verify_installation_otp.request

data class VerifyInstallationOtpRequest(
    var id: String?,
    var type: String?,
    var otp: String?
)