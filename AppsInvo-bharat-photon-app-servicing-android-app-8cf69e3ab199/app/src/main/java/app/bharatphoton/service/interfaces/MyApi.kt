package app.bharatphoton.service.interfaces

import app.bharatphoton.service.api_model_classes.AwsCredentialResponse
import app.bharatphoton.service.api_model_classes.CmsResponse
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponse
import app.bharatphoton.service.api_model_classes.ComplaintSinDetailsResponse
import app.bharatphoton.service.api_model_classes.PendingSinDetailsResponse
import app.bharatphoton.service.api_model_classes.complaint_update_sin.request.ComplaintUpdateSinRequest
import app.bharatphoton.service.api_model_classes.complaint_update_sin.response.ComplaintUpdateSinResponse
import app.bharatphoton.service.api_model_classes.help_support.request.HelpSupportRequest
import app.bharatphoton.service.api_model_classes.help_support.response.HelpSupportResponse
import app.bharatphoton.service.api_model_classes.login.request.LoginRequest
import app.bharatphoton.service.api_model_classes.login.response.LoginResponse
import app.bharatphoton.service.api_model_classes.NotificationResponse
import app.bharatphoton.service.api_model_classes.ProductComplaintListingResponse
import app.bharatphoton.service.api_model_classes.ProductDetailResponse
import app.bharatphoton.service.api_model_classes.ProductInstallationList
import app.bharatphoton.service.api_model_classes.ProfileDetailsResponse
import app.bharatphoton.service.api_model_classes.resend_installation_otp.request.ResendInstallationOtpRequest
import app.bharatphoton.service.api_model_classes.resend_installation_otp.response.ResendInstallationOtpResponse
import app.bharatphoton.service.api_model_classes.SinDetailsResponse
import app.bharatphoton.service.api_model_classes.SubscriptionDetailsResponse
import app.bharatphoton.service.api_model_classes.SubscriptionInstallationListResponse
import app.bharatphoton.service.api_model_classes.subscription_update_sin.request.SubscriptionUpdateRequest
import app.bharatphoton.service.api_model_classes.subscription_update_sin.response.SubscriptionUpdateResponse
import app.bharatphoton.service.api_model_classes.update_device_token.request.UpdateDeviceTokenRequest
import app.bharatphoton.service.api_model_classes.update_device_token.response.UpdateDeviceTokenResponse
import app.bharatphoton.service.api_model_classes.update_sin.request.UpdateSinRequest
import app.bharatphoton.service.api_model_classes.update_sin.response.UpdateSinResponse
import app.bharatphoton.service.api_model_classes.verify_complaint_otp.request.ComplaintOtpRequest
import app.bharatphoton.service.api_model_classes.verify_complaint_otp.response.ComplaintOtpResponse
import app.bharatphoton.service.api_model_classes.verify_installation_otp.request.VerifyInstallationOtpRequest
import app.bharatphoton.service.api_model_classes.verify_installation_otp.response.VerifyInstallationOtpResponse
import app.bharatphoton.service.api_model_classes.WarrantyListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface MyApi {

    @GET("s3")
    fun awsCredential() : Call<AwsCredentialResponse>

    @POST("staff/loginStaff")
    fun login(@Body login: LoginRequest) : Call<LoginResponse>

    @GET("staff/productInstallationListing")
    fun productInstallationListing(
        @Header("authtoken") token: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("installationStatus") installationStatus: String?,
        @Query("id") productId: String?) : Call<ProductInstallationList>

    @GET("staff/complaintList")
    fun complaintList(
        @Header("authtoken") token: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("complaintStatus") complaintStatus: String?) : Call<ProductComplaintListingResponse>

    @GET("staff/subInstallationList")
    fun subInstallationList(
        @Header("authtoken") token: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("installationStatus") installationStatus: String?,
        @Query("id") productId: String?) : Call<SubscriptionInstallationListResponse>

    @GET("staff/productInstallationListing")
    fun productDetails(
        @Header("authtoken") token: String,
        @Query("installationStatus") installationStatus: String?,
        @Query("id") productId: String?) : Call<ProductDetailResponse>

    @GET("staff/complaintList")
    fun complaintDetails(
        @Header("authtoken") token: String,
        @Query("complaintStatus") complaintStatus: String?,
        @Query("id") complaintId: String?) : Call<ComplaintDetailsResponse>

    @GET("staff/subInstallationList")
    fun subscriptionDetails(
        @Header("authtoken") token: String,
        @Query("installationStatus") installationStatus: String?,
        @Query("id") subscriptionId: String?) : Call<SubscriptionDetailsResponse>

    @POST("staff//updateSin")
    fun updateSin(
        @Header("authtoken") token: String,
        @Body updateSin: UpdateSinRequest?) : Call<UpdateSinResponse>

    @POST("staff/updateSubSin")
    fun updateSubSin(
        @Header("authtoken") token: String,
        @Body updateSubSin: SubscriptionUpdateRequest?) : Call<SubscriptionUpdateResponse>

    @POST("staff/updateComplaintSin")
    fun updateComplaintSin(
        @Header("authtoken") token: String,
        @Body updateComplaintSin: ComplaintUpdateSinRequest?) : Call<ComplaintUpdateSinResponse>

    @POST("staff/verifyInstallationOtp")
    fun verifyInstallationOtp(
        @Header("authtoken") token: String,
        @Body verifyInstallationOtp: VerifyInstallationOtpRequest?) : Call<VerifyInstallationOtpResponse>

    @POST("staff/verifyComplaintOtp")
    fun verifyComplaintOtp(
        @Header("authtoken") token: String,
        @Body verifyComplaintOtp: ComplaintOtpRequest?) : Call<ComplaintOtpResponse>

    @POST("staff/resendInstallationOtp")
    fun resendInstallationOtp(
        @Header("authtoken") token: String,
        @Body resendInstallationOtp: ResendInstallationOtpRequest?) : Call<ResendInstallationOtpResponse>

    @GET("staff/cms")
    fun cms(
        @Header("authtoken") token: String,
        @Query("type") cmsType: String?) : Call<CmsResponse>

    @GET("staff/sinDetails")
    fun sinDetails(
        @Header("authtoken") token: String,
        @Query("id") sinId: String?) : Call<SinDetailsResponse>

    @GET("staff/comlaintSinDetails")
    fun complaintSinDetails(
        @Header("authtoken") token: String,
        @Query("userId") userId: String?,
        @Query("productId") productId: String?,
        @Query("complaintId") complaintId: String?,
        @Query("status") status: String?) : Call<ComplaintSinDetailsResponse>

    @GET("staff/comlaintSinDetails")
    fun complaintPendingSinDetails(
        @Header("authtoken") token: String,
        @Query("complaintId") complaintId: String?,
        @Query("status") status: String?) : Call<PendingSinDetailsResponse>

    @GET("staff/profileDetails")
    fun profileDetails(
        @Header("authtoken") token: String) : Call<ProfileDetailsResponse>

    @POST("staff/addHelp")
    fun addHelp(
        @Header("authtoken") token: String,
        @Body addHelp: HelpSupportRequest?) : Call<HelpSupportResponse>

    @GET("staff/warrentyList")
    fun warrantyList(
        @Header("authtoken") token: String,
        @Query("productId") productId: String?) : Call<WarrantyListResponse>

    @PATCH("staff/updateDeviceToken")
    fun updateDeviceToken(@Header("authtoken") token: String,
                          @Body updateToken: UpdateDeviceTokenRequest): Call<UpdateDeviceTokenResponse>

    @GET("staff/notificationList")
    fun notificationList(
        @Header("authtoken") token: String,
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("read") read: String?) : Call<NotificationResponse>

    /*
    try {

    }catch (e: Exception){

    }
    */
}