package app.bharatphoton.service.api_model_classes

import app.bharatphoton.service.api_model_classes.login.response.Count

data class AwsCredentialResponse(
    var code: Int?,
    var count: Count?,
    var `data`: AwsCredentialResponseData?,
    var message: String?
)
data class AwsCredentialResponseData(
    var accessKeyId: String?,
    var bucketName: String?,
    var region: String?,
    var secretAccessKey: String?
)