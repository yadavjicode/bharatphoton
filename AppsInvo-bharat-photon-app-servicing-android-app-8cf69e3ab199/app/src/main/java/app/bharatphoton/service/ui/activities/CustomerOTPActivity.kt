package app.bharatphoton.service.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.error401.Error401
import app.bharatphoton.service.api_model_classes.resend_installation_otp.request.ResendInstallationOtpRequest
import app.bharatphoton.service.api_model_classes.resend_installation_otp.response.ResendInstallationOtpResponse
import app.bharatphoton.service.api_model_classes.verify_complaint_otp.request.ComplaintOtpRequest
import app.bharatphoton.service.api_model_classes.verify_complaint_otp.response.ComplaintOtpResponse
import app.bharatphoton.service.api_model_classes.verify_installation_otp.request.VerifyInstallationOtpRequest
import app.bharatphoton.service.api_model_classes.verify_installation_otp.response.VerifyInstallationOtpResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityCustomerOtpactivityBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COMPLAINT_SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.FROM_ACTIVITY
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.CrashlyticsConstants.OTP_VERIFICATION
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_SIN_UPDATE
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CustomerOTPActivity : BaseActivity() {
    private lateinit var binding: ActivityCustomerOtpactivityBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var complaintId: String? = null
    private var updateComplaintSinId: String? = null
    private var updateSinId: String? = null
    private var fromActivity: String? = null
    private var otpForApi: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(OTP_VERIFICATION,"CustomerOTPActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")
        complaintId = intent.getStringExtra(COMPLAINT_ID)
        updateComplaintSinId = intent.getStringExtra(COMPLAINT_SIN_UPDATE_ID)
        updateSinId = intent.getStringExtra(SIN_UPDATE_ID)
        fromActivity = intent.getStringExtra(FROM_ACTIVITY)

        applySlideInAnimation(binding.clCustomerOtp)

        binding.tvBtnVerify.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation

            val otp = binding.et1.text.toString() + binding.et2.text.toString() +
                    binding.et3.text.toString() + binding.et4.text.toString() +
                    binding.et5.text.toString() + binding.et6.text.toString()
            otpForApi = otp

            if (binding.et1.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et2.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et3.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et4.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et5.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et6.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else{
                when(fromActivity){
                    "CustomerRemarksActivity" -> {
                        verifyInstallationOtpApiCall("order")
                    }
                    "ComplaintCustomerRemarkActivity" -> {
                        updateSinId = updateComplaintSinId
                        verifyComplaintOtpApiCall()
                    }
                    "SubscriptionCustomerRemarkActivity" -> {
                        verifyInstallationOtpApiCall("subscription")
                    }
                }
            }
        }

        binding.ivBack.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
//            finish()
            val intent = Intent(this@CustomerOTPActivity, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }

        val editTexts = listOf(binding.et1, binding.et2, binding.et3, binding.et4, binding.et5, binding.et6)
        val views = listOf(binding.v1, binding.v2, binding.v3, binding.v4, binding.v5, binding.v6)

        for (i in editTexts.indices) {
            val currentEditText = editTexts[i]
            val nextEditText = if (i < editTexts.lastIndex) editTexts[i + 1] else null
            val previousEditText = if (i > 0) editTexts[i - 1] else null
            currentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    if (editable.length == 1 && nextEditText != null) {
                        nextEditText.requestFocus()
                    }
                    if (editable.isNotEmpty()) {
                        views[i].setBackgroundColor(ContextCompat.getColor(this@CustomerOTPActivity, R.color.photon_orange))
                    }
                    else {
                        views[i].setBackgroundColor(ContextCompat.getColor(this@CustomerOTPActivity, R.color.black))
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            currentEditText.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentEditText.text.isEmpty() && previousEditText != null) {
                    previousEditText.text = null
                    previousEditText.requestFocus()
                    true
                } else {
                    false
                }
            }
        }

        resendOtp()
        binding.tvResendOtp.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            when(fromActivity){
                "CustomerRemarksActivity" -> {
                    resendInstallationOtpApiCall(updateSinId)
                }
                "ComplaintCustomerRemarkActivity" -> {
                    resendInstallationOtpApiCall(updateComplaintSinId)
                }
                "SubscriptionCustomerRemarkActivity" -> {
                    resendInstallationOtpApiCall(updateSinId)
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@CustomerOTPActivity, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            }
        })
    }

    private fun resendOtp(){
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendOtp.isClickable=false
                binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@CustomerOTPActivity, R.color.photon_dark_gray))
                binding.tvTimer.text = "00:"+"%02d".format(millisUntilFinished / 1000)
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvResendOtp.isClickable=true
                binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@CustomerOTPActivity, R.color.photon_orange))
            }
        }.start()
    }
    private fun verifyInstallationOtpApiCall(type: String) {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val requestBody = VerifyInstallationOtpRequest(updateSinId,type , otpForApi)
        val getDetail = retrofitBuilder.verifyInstallationOtp(token, requestBody)
        getDetail.enqueue(object : Callback<VerifyInstallationOtpResponse> {
            override fun onResponse(call: Call<VerifyInstallationOtpResponse>, response: Response<VerifyInstallationOtpResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        showAnimatedCustomToast(response.body()?.message ?: "")

                        val intent = Intent(this@CustomerOTPActivity,HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    else if (response.code() == 400) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.code() == 401) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.body()?.code == 0){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@CustomerOTPActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("verifyInstallationOtpApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<VerifyInstallationOtpResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@CustomerOTPActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("verifyInstallationOtpApiCall-API call failed ${e.message}")
                }
            }
        })
    }
    private fun verifyComplaintOtpApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val requestBody = ComplaintOtpRequest(complaintId, updateComplaintSinId, otpForApi)
        val getDetail = retrofitBuilder.verifyComplaintOtp(token, requestBody)
        getDetail.enqueue(object : Callback<ComplaintOtpResponse> {
            override fun onResponse(call: Call<ComplaintOtpResponse>, response: Response<ComplaintOtpResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        showAnimatedCustomToast(response.body()?.message ?: "")

                        val intent = Intent(this@CustomerOTPActivity,HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    else if (response.code() == 400) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.code() == 401) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.body()?.code == 0){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@CustomerOTPActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("verifyComplaintOtpApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ComplaintOtpResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@CustomerOTPActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("verifyComplaintOtpApiCall-API call failed ${e.message}")
                }
            }
        })
    }
    private fun resendInstallationOtpApiCall(updateId: String?) {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val requestBody = ResendInstallationOtpRequest(updateId)
        val getDetail = retrofitBuilder.resendInstallationOtp(token, requestBody)
        getDetail.enqueue(object : Callback<ResendInstallationOtpResponse> {
            override fun onResponse(call: Call<ResendInstallationOtpResponse>, response: Response<ResendInstallationOtpResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                        resendOtp()
                    }
                    else if (response.code() == 401) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.body()?.code == 0){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@CustomerOTPActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("resendInstallationOtpApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ResendInstallationOtpResponse>, t: Throwable) {
                try {
                    ApiErrorHandler.handleException(this@CustomerOTPActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("resendInstallationOtpApiCall-API call failed ${e.message}")
                }
                binding.clLoader.isVisible = false
            }
        })
    }
}