package app.bharatphoton.service.ui.activities.product_complaint

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.complaint_update_sin.request.ComplaintUpdateSinRequest
import app.bharatphoton.service.api_model_classes.complaint_update_sin.request.ComplaintUpdateSinRequestImage
import app.bharatphoton.service.api_model_classes.complaint_update_sin.response.ComplaintUpdateSinResponse
import app.bharatphoton.service.api_model_classes.error401.Error401
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityComplaintCustomerRemarkBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.CustomerOTPActivity
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.ALL_MEDIA_URLS
import app.bharatphoton.service.utils.Constants.BATTERY_VOLTAGE
import app.bharatphoton.service.utils.Constants.CLICKED_MEDIA_URL
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COMPLAINT_SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.COST_SAVED
import app.bharatphoton.service.utils.Constants.CUSTOMER_ID
import app.bharatphoton.service.utils.Constants.CUSTOMER_NAME
import app.bharatphoton.service.utils.Constants.CUT_OFF_DELAY
import app.bharatphoton.service.utils.Constants.FIN_NO
import app.bharatphoton.service.utils.Constants.FLUSH_TIME
import app.bharatphoton.service.utils.Constants.FROM_ACTIVITY
import app.bharatphoton.service.utils.Constants.INSTALLATION_IMAGE
import app.bharatphoton.service.utils.Constants.INSTALLATION_IMAGE_ID
import app.bharatphoton.service.utils.Constants.IS_FIELD_AVAILABLE
import app.bharatphoton.service.utils.Constants.IS_SPECIFIC_CATEGORY_FOR_FLUSH
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.OFF_DELAY
import app.bharatphoton.service.utils.Constants.PRE_FLUSH
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.Constants.RANGE
import app.bharatphoton.service.utils.Constants.REMARK
import app.bharatphoton.service.utils.Constants.RESOLVE_ACTION
import app.bharatphoton.service.utils.Constants.RESOLVE_DATE
import app.bharatphoton.service.utils.Constants.SIN_NO
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.TOTAL_PAYMENT
import app.bharatphoton.service.utils.Constants.USER_COUNT
import app.bharatphoton.service.utils.Constants.WATER_SAVED
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_SIN_UPDATE
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.CustomMediaPickerWithCamera
import app.bharatphoton.service.utils.MediaViewerDialogFragment
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ComplaintCustomerRemarkActivity : BaseActivity() {
    private lateinit var binding: ActivityComplaintCustomerRemarkBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var mediaUrl: String? = null
    private var requestBody: ComplaintUpdateSinRequest? = null

    private val mediaPicker = CustomMediaPickerWithCamera(this, object : CustomMediaPickerWithCamera.MediaPickerCallback {
        @SuppressLint("SetTextI18n")
        override fun onMediaSelected(mediaUrl: String) {
            this@ComplaintCustomerRemarkActivity.mediaUrl = mediaUrl
            val mediaUrlGet = mediaUrl
            val progressDialog = setProgressDialog(this@ComplaintCustomerRemarkActivity)
            if (mediaUrlGet.isNotEmpty()) {
                binding.llUploadPhotoVideo.isVisible = false
                binding.tvBtnChoose.text = "Choose Another File"
            } else {
                binding.llUploadPhotoVideo.isVisible = true
                binding.tvBtnChoose.text = "Choose File"
            }
            binding.clLoader.isVisible = true
            Glide.with(this@ComplaintCustomerRemarkActivity).load(mediaUrlGet).placeholder(R.drawable.default_gray_image_img).error(R.drawable.default_gray_image_img).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean
                    ): Boolean {
                        binding.clLoader.isVisible = false
                        progressDialog.dismiss()
                        showAnimatedCustomToast("Glide load failed $e")
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean
                    ): Boolean {
                        binding.clLoader.isVisible = false
                        progressDialog.dismiss()
                        return false
                    }
                }).into(binding.ivUploadImg)

            if (!this@ComplaintCustomerRemarkActivity.mediaUrl.isNullOrEmpty()) {
                if (isVideo(this@ComplaintCustomerRemarkActivity.mediaUrl!!)) {
                    binding.ivPlay.visibility = View.VISIBLE
                } else {
                    binding.ivPlay.visibility = View.GONE
                }
            }else{
                binding.ivPlay.visibility = View.GONE
            }

            progressDialog.dismiss()
        }
    })

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (!mediaUrl.isNullOrEmpty()) {
            binding.llUploadPhotoVideo.isVisible = false
            binding.tvBtnChoose.text = "Choose Another File"
        } else {
            binding.llUploadPhotoVideo.isVisible = true
            binding.tvBtnChoose.text = "Choose File"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintCustomerRemarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(PRODUCT_COMPLAINT_SIN_UPDATE,"ComplaintCustomerRemarkActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")

        getStorageDetailsApiCall(this)

        val complaintId = intent.extras?.getString(COMPLAINT_ID)
        val customerId = intent.extras?.getString(CUSTOMER_ID)
        "customerId-> $customerId".mLog()
        val productId = intent.extras?.getString(PRODUCT_ID)
        val fin = intent.extras?.getString(FIN_NO)
        val sin = intent.extras?.getString(SIN_NO)
        val userCount = intent.extras?.getString(USER_COUNT)
        val range = intent.extras?.getString(RANGE)
        val preFlush = intent.extras?.getString(PRE_FLUSH)
        val flushTime = intent.extras?.getString(FLUSH_TIME)
        val offDelay = intent.extras?.getString(OFF_DELAY)
        val cutOffDelay = intent.extras?.getString(CUT_OFF_DELAY)
        val batteryVoltage = intent.extras?.getString(BATTERY_VOLTAGE)
        val battery = batteryVoltage?.toDoubleOrNull()?:0.0
        val paymentDone = intent.extras?.getString(TOTAL_PAYMENT)
        val installationImage = intent.extras?.getString(INSTALLATION_IMAGE)
        val installationImageId = intent.extras?.getString(INSTALLATION_IMAGE_ID)
        val remark = intent.extras?.getString(REMARK)
        val customerName = intent.extras?.getString(CUSTOMER_NAME)
        val resolveDate = intent.extras?.getString(RESOLVE_DATE)
        val resolveAction = intent.extras?.getString(RESOLVE_ACTION)
        val waterSaved = intent.extras?.getString(WATER_SAVED)
        val costSaved = intent.extras?.getString(COST_SAVED)
        val updateId = intent.extras?.getString(SIN_UPDATE_ID)
        val isFieldAvailable = intent.extras?.getString(IS_FIELD_AVAILABLE)
        val isSpecificForFlush = intent.extras?.getString(IS_SPECIFIC_CATEGORY_FOR_FLUSH)

        mediaUrl = installationImage

        if (!mediaUrl.isNullOrEmpty()) {
            if (isVideo(mediaUrl!!)) {
                binding.ivPlay.visibility = View.VISIBLE
            } else {
                binding.ivPlay.visibility = View.GONE
            }
        }else{
            binding.ivPlay.visibility = View.GONE
        }

        if (!mediaUrl.isNullOrEmpty()) {
            Glide.with(this).load(mediaUrl).into(binding.ivUploadImg)
        }
        binding.etCustomerRemark.setText(remark)
        binding.etCustomerName.setText(customerName)

        if (!mediaUrl.isNullOrEmpty()) {
            binding.llUploadPhotoVideo.isVisible = false
            binding.tvBtnChoose.text = "Choose Another File"
        } else {
            binding.llUploadPhotoVideo.isVisible = true
            binding.tvBtnChoose.text = "Choose File"
        }

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
        binding.ivUploadImg.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            if (mediaUrl != null) {
                val remarkPhotos: ArrayList<String> = ArrayList()
                remarkPhotos.add(mediaUrl!!)

                val bundle = Bundle().apply {
                    putString(CLICKED_MEDIA_URL, mediaUrl)
                    putStringArrayList(ALL_MEDIA_URLS, remarkPhotos)
                }
                val dialogFragment = MediaViewerDialogFragment()
                dialogFragment.arguments = bundle
                dialogFragment.show(supportFragmentManager, "MediaViewerDialog")
            } else {
                showAnimatedCustomToast("No Media Found.")
            }
        }
//        applySlideInAnimation(binding.clComplaintRemark)

        binding.tvBtnChoose.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            if (isCameraPermissionGranted()) {
                mediaPicker.showMediaPickerDialog(this, binding.clComplaintRemark)
            } else {
                requestCameraPermission()
            }
        }

        /*binding.tvBtnResolveComplaint.setOnClickListener {
            showHideButton(false)

            val customerRemark = binding.etCustomerRemark.text.toString()
            val etCustomerName = binding.etCustomerName.text.toString()

            val image: ArrayList<ComplaintUpdateSinRequestImage> = ArrayList()
            image.add(ComplaintUpdateSinRequestImage(installationImageId, mediaUrl))

            if (isFieldAvailable == "0" && isSpecificForFlush == "0"){
                requestBody = ComplaintUpdateSinRequest(null, complaintId = complaintId, null, customerId,
                    etCustomerName, null, id = updateId, image, null, productId, null, customerRemark, paymentDone,
                    null, null, fin, sin, resolveDate, resolveAction, null, null)
            }
            else if (isFieldAvailable == "0" && isSpecificForFlush == "1"){
                requestBody = ComplaintUpdateSinRequest(batteryVoltage, complaintId = complaintId, costSaved, customerId,
                    etCustomerName, flushTime, id = updateId, image, preFlush, productId, range, customerRemark, paymentDone,
                    userCount, waterSaved, fin, sin, resolveDate, resolveAction, null, null)
            }
            else if (isFieldAvailable == "1" && isSpecificForFlush == "0"){
                requestBody = ComplaintUpdateSinRequest(batteryVoltage, complaintId = complaintId, costSaved, customerId,
                    etCustomerName, null, id = updateId, image, null, productId, range, customerRemark, paymentDone,
                    userCount, waterSaved, fin, sin, resolveDate, resolveAction, offDelay, cutOffDelay)
            }
            else if (isFieldAvailable == "1" && isSpecificForFlush == "1"){
                requestBody = ComplaintUpdateSinRequest(batteryVoltage, complaintId = complaintId, costSaved, customerId,
                    etCustomerName, flushTime, id = updateId, image, preFlush, productId, range, customerRemark, paymentDone,
                    userCount, waterSaved, fin, sin, resolveDate, resolveAction, offDelay, cutOffDelay)
            }

            if (customerRemark.isBlank()) {
                showAnimatedCustomToast("Please fill remark!")
                showHideButton(true)
                return@setOnClickListener
            } else if (etCustomerName.isBlank()) {
                showAnimatedCustomToast("Please fill name!")
                showHideButton(true)
                return@setOnClickListener
            } else if (mediaUrl.isNullOrEmpty()) {
                showAnimatedCustomToast("Please add image or video!")
                showHideButton(true)
                return@setOnClickListener
            } else {
                complaintSinUpdateApiCall(complaintId)
                showHideButton(true)
            }
        }*/

        binding.tvBtnResolveComplaint.setOnClickListener {
            enableVisibleButton(false)

            val customerRemark = binding.etCustomerRemark.text.toString()
            val etCustomerName = binding.etCustomerName.text.toString()
            val image = arrayListOf(ComplaintUpdateSinRequestImage(installationImageId, mediaUrl))

            if (customerRemark.isBlank()) {
                showAnimatedCustomToast("Please fill remark!")
                enableVisibleButton(true)
                return@setOnClickListener
            }

            if (etCustomerName.isBlank()) {
                showAnimatedCustomToast("Please fill name!")
                enableVisibleButton(true)
                return@setOnClickListener
            }

            if (mediaUrl.isNullOrEmpty()) {
                showAnimatedCustomToast("Please add image or video!")
                enableVisibleButton(true)
                return@setOnClickListener
            }

            requestBody = when {
                isFieldAvailable == "0" && isSpecificForFlush == "0" -> {
                    ComplaintUpdateSinRequest(
                        null, complaintId, null, customerId, etCustomerName, null,
                        id = updateId, image = image, null, productId, null, customerRemark,
                        paymentDone, null, null, fin, sin, resolveDate, resolveAction, null, null
                    )
                }
                isFieldAvailable == "0" && isSpecificForFlush == "1" -> {
                    ComplaintUpdateSinRequest(
                        battery?:0.0, complaintId, costSaved, customerId, etCustomerName, flushTime,
                        id = updateId, image = image, preFlush, productId, range, customerRemark,
                        paymentDone, userCount, waterSaved, fin, sin, resolveDate, resolveAction, null, null
                    )
                }
                isFieldAvailable == "1" && isSpecificForFlush == "0" -> {
                    ComplaintUpdateSinRequest(
                        battery?:0.0, complaintId, costSaved, customerId, etCustomerName, null,
                        id = updateId, image = image, null, productId, range, customerRemark,
                        paymentDone, userCount, waterSaved, fin, sin, resolveDate, resolveAction, offDelay, cutOffDelay
                    )
                }
                else -> {
                    ComplaintUpdateSinRequest(
                        battery?:0.0, complaintId, costSaved, customerId, etCustomerName, flushTime,
                        id = updateId, image = image, preFlush, productId, range, customerRemark,
                        paymentDone, userCount, waterSaved, fin, sin, resolveDate, resolveAction, offDelay, cutOffDelay
                    )
                }
            }

            if (complaintId != null) {
                complaintSinUpdateApiCall(complaintId)
            } else {
                showAnimatedCustomToast("Complaint Id Not Available.")
                enableVisibleButton(true)
            }
        }

        val editTexts = listOf(binding.etCustomerRemark, binding.etCustomerName)
        setCursorToEndOnFocus(editTexts)
    }

    private fun complaintSinUpdateApiCall(complaintId: String?) {
        binding.clLoader.isVisible = true
        binding.tvBtnResolveComplaint.isEnabled = false
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.updateComplaintSin(token, requestBody)
        getDetail.enqueue(object : Callback<ComplaintUpdateSinResponse> {
            override fun onResponse(call: Call<ComplaintUpdateSinResponse>, response: Response<ComplaintUpdateSinResponse>) {
                binding.clLoader.isVisible = false
                enableVisibleButton(true)
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1) {
                        showAnimatedCustomToast(response.body()?.message ?: "")
                        val updateComplaintSinId = response.body()?.data?._id.toString()
                        val intent = Intent(this@ComplaintCustomerRemarkActivity, CustomerOTPActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra(COMPLAINT_SIN_UPDATE_ID, updateComplaintSinId)
                        intent.putExtra(COMPLAINT_ID, complaintId)
                        intent.putExtra(FROM_ACTIVITY, "ComplaintCustomerRemarkActivity")
                        startActivity(intent)
                        finish()
                    } else if (response.code() == 401) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    } else if (response.body()?.code == 0) {
                        showAnimatedCustomToast(response.body()?.message ?: "")
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@ComplaintCustomerRemarkActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("complaintSinUpdateApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ComplaintUpdateSinResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                enableVisibleButton(true)
                try {
                    ApiErrorHandler.handleException(this@ComplaintCustomerRemarkActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("complaintSinUpdateApiCall-API call failed ${e.message}")
                }
            }
        })
    }

    private fun enableVisibleButton(status : Boolean){
        binding.tvBtnResolveComplaint.apply {
            isEnabled = status
            isVisible = status
            isClickable = status
            isFocusable = status
        }
    }
}