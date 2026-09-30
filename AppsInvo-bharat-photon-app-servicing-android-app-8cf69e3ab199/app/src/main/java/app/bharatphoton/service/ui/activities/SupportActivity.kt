package app.bharatphoton.service.ui.activities

import android.os.Bundle
import androidx.core.view.isVisible
import app.bharatphoton.service.api_model_classes.error401.Error401
import app.bharatphoton.service.api_model_classes.help_support.request.HelpSupportRequest
import app.bharatphoton.service.api_model_classes.help_support.response.HelpSupportResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivitySupportBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.SUPPORT
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

class SupportActivity : BaseActivity() {
    private lateinit var binding : ActivitySupportBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(SUPPORT,"SupportActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")


        applySlideInAnimation(binding.clSupport)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
        binding.tvBtnSubmit.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            if (binding.etSubject.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter subject!")
            }
            else if (binding.etConcern.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter your concern!")
            }
            else {
                addSupportApiCall()
            }
        }

        val editTexts = listOf(binding.etSubject, binding.etConcern)
        setCursorToEndOnFocus(editTexts)
    }

    private fun addSupportApiCall() {
        val subject = binding.etSubject.text.toString().trim()
        val message = binding.etConcern.text.toString().trim()
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val requestBody = HelpSupportRequest(message = message, subject = subject)
        val getDetail = retrofitBuilder.addHelp(token, requestBody)
        getDetail.enqueue(object : Callback<HelpSupportResponse> {
            override fun onResponse(call: Call<HelpSupportResponse>, response: Response<HelpSupportResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        showAnimatedCustomToast("Form Submitted!")
                        finish()
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
                        ApiErrorHandler.handleException(this@SupportActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("addSupportApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<HelpSupportResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@SupportActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("addSupportApiCall-API call failed ${e.message}")
                }
            }
        })
    }
}