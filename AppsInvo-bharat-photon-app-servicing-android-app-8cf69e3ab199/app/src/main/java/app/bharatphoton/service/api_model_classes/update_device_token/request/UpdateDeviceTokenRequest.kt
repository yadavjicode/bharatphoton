package app.bharatphoton.service.api_model_classes.update_device_token.request

data class UpdateDeviceTokenRequest(
    var deviceId: String?,
    var deviceToken: String?,
    var deviceType: String?
)