package app.bharatphoton.service.api_model_classes

data class NotificationResponse(
    var code: Int?,
    var count: Int?,
    var `data`: List<NotificationResponseData>?,
    var message: String?
)
data class NotificationResponseData(
    var __v: Int?,
    var _id: String?,
    var body: String?,
    var createdAt: String?,
    var is_Active: Boolean?,
    var is_deleted: Boolean?,
    var profileImage: String?,
    var read: Int?,
    var staffId: String?,
    var staffNotification: Boolean?,
    var title: String?,
    var updatedAt: String?
)