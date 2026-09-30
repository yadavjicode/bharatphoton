package app.bharatphoton.service.utils

import app.bharatphoton.service.utils.Constants.S3_BUCKET_KEY
import app.bharatphoton.service.utils.Constants.S3_BUCKET_NAME
import app.bharatphoton.service.utils.Constants.S3_BUCKET_REGION
import app.bharatphoton.service.utils.Constants.S3_BUCKET_SECRET
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AmazonS3Uploader {
    companion object{
        suspend fun uploadFile(file: File?) : String? = withContext(Dispatchers.IO){
            var url: String? = null

            kotlin.runCatching {

                val S3_BUCKET_KEY = S3_BUCKET_KEY
                val S3_BUCKET_REGION = S3_BUCKET_REGION
                val S3_BUCKET_SECRET = S3_BUCKET_SECRET
                val S3_BUCKET_NAME = S3_BUCKET_NAME

                val credentials = BasicAWSCredentials(S3_BUCKET_KEY, S3_BUCKET_SECRET)
                val s3Client: AmazonS3 = AmazonS3Client(credentials)
                s3Client.setRegion(com.amazonaws.regions.Region.getRegion(S3_BUCKET_REGION))

                val objectKey = "androidImages/${file?.name}"
                val request = PutObjectRequest(S3_BUCKET_NAME, objectKey, file)
                request.cannedAcl = CannedAccessControlList.PublicRead
                s3Client.putObject(request)
                url  = s3Client.getUrl(S3_BUCKET_NAME, objectKey).toString()
            }.onSuccess {
                return@withContext url
            }.onFailure {
                return@withContext null
            }
            return@withContext  url
        }
    }
}