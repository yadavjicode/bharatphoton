package app.bharatphoton.service.api_model_classes

data class CmsResponse(
    var code: Int?,
    var count: Int?,
    var `data`: List<CmsResponseData?>?,
    var message: String?
)
data class CmsResponseData(
    var _id: String?,
    var createdAt: String?,
    var descriptions: String?,
    var is_status: Int?,
    var title: String?,
    var type: String?,
    var updatedAt: String?,
    var userType: Int?
)