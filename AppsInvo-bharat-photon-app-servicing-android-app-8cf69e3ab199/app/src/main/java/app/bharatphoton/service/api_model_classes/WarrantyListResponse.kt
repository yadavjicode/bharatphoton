package app.bharatphoton.service.api_model_classes

import app.bharatphoton.service.api_model_classes.login.response.Count

data class WarrantyListResponse(
    var code: Int?,
    var count: Count?,
    var `data`: WarrantyListResponseData?,
    var message: String?
)
data class WarrantyListResponseData(
    var _id: String?,
    var warrentyMonth: String?
)