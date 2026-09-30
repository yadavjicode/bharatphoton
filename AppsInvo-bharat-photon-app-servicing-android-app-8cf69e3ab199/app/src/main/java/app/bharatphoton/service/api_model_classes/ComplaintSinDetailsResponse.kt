package app.bharatphoton.service.api_model_classes

data class ComplaintSinDetailsResponse(
    var code: Int?,
    var count: Int?,
    var `data`: ComplaintSinDetailsResponseData?,
    var message: String?
)
data class ComplaintSinDetailsResponseData(
    var complaintDetails: ComplaintSinDetailsResponseComplaintDetails?,
    var sinDetails: ComplaintSinDetailsResponseSinDetails?
)
data class ComplaintSinDetailsResponseComplaintDetails(
    var _id: String?,
    var categoryId: String?,
    var complaintReason: String?,
    var complaintStatus: Int?,
    var createdAt: String?,
    var description: String?,
    var image: List<ComplaintSinDetailsResponseImage?>?,
    var is_deleted: Boolean?,
    var productDescription: String?,
    var productFinNo: String?,
    var productId: String?,
    var productImage: List<ComplaintSinDetailsResponseProductImage?>?,
    var productModelNo: String?,
    var productName: String?,
    var resolveDate: String?,
    var sin: String?,
    var staffId: String?,
    var status: Boolean?,
    var updatedAt: String?,
    var userId: String?,
    var uuid: String?
)
data class ComplaintSinDetailsResponseImage(
    var _id: String?,
    var url: String?
)
data class ComplaintSinDetailsResponseProductImage(
    var _id: String?,
    var url: String?
)
data class ComplaintSinDetailsResponseSinDetails(
    var _id: String?,
    var batteryVoltage: String?,
    var customerId: String?,
    var flushTime: String?,
    var preflush: String?,
    var offDelay: String?,
    var cutOffDelay: String?,
    var productId: String?,
    var range: String?,
    var sin: String?,
    var finNo: String?,
    var userCount: String?,
    var waterSaved: String?,
    var costSaved: String?,
    var totalPayment: String?
)