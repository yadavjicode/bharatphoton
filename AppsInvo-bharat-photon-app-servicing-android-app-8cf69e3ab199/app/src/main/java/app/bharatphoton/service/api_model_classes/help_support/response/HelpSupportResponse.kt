package app.bharatphoton.service.api_model_classes.help_support.response

import app.bharatphoton.service.api_model_classes.login.response.Count

data class HelpSupportResponse(
    var code: Int?,
    var count: Count?,
    var `data`: HelpSupportResponseData?,
    var message: String?
)
data class HelpSupportResponseData(
    var _id: String?,
    var countryCode: Int?,
    var createdAt: String?,
    var is_Active: Boolean?,
    var is_deleted: Boolean?,
    var message: String?,
    var staff: Int?,
    var subject: String?,
    var updatedAt: String?
)