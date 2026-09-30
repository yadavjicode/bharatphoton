package app.bharatphoton.service.api_model_classes

import app.bharatphoton.service.api_model_classes.login.response.Count

data class ProfileDetailsResponse(
    var code: Int?,
    var count: Count?,
    var `data`: ProfileDetailsResponseData?,
    var message: String?
)
data class ProfileDetailsResponseData(
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