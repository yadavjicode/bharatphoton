package app.bharatphoton.service.api_model_classes.login.response

data class LoginResponse(
    var code: Int?,
    var count: Count?,
    var `data`: LoginResponseData?,
    var message: String?
)
class Count
data class LoginResponseData(
    var auth_token: String?,
    var result: LoginResponseResult?
)
data class LoginResponseResult(
    var _id: String?,
    var country_code: String?,
    var createdAt: String?,
    var deviceId: String?,
    var deviceToken: String?,
    var deviceType: Any?,
    var email: String?,
    var facebookId: String?,
    var firstName: String?,
    var googleId: String?,
    var isActive: Boolean?,
    var isDeleted: Boolean?,
    var isPhoneVerified: Boolean?,
    var isemailVerified: Boolean?,
    var lastName: String?,
    var password: String?,
    var phoneNo: Long?,
    var profileImage: String?,
    var updatedAt: String?
)