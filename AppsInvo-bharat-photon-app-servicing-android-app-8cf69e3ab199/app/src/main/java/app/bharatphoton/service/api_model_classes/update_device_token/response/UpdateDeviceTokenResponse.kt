package app.bharatphoton.service.api_model_classes.update_device_token.response

import app.bharatphoton.service.api_model_classes.login.response.Count

data class UpdateDeviceTokenResponse(
    var code: Int?,
    var count: Count?,
    var `data`: UpdateDeviceTokenResponseData?,
    var message: String?
)
data class UpdateDeviceTokenResponseData(
    var __v: Int?,
    var _id: String?,
    var country_code: String?,
    var createdAt: String?,
    var deviceId: String?,
    var deviceToken: String?,
    var deviceType: String?,
    var email: String?,
    var facebookId: String?,
    var firstName: String?,
    var googleId: String?,
    var isActive: Boolean?,
    var isDeleted: Boolean?,
    var isPhoneVerified: Boolean?,
    var isemailVerified: Boolean?,
    var lastName: String?,
    var otp: String?,
    var password: String?,
    var phoneNo: Long?,
    var profileImage: String?,
    var rating: Int?,
    var updatedAt: String?
)