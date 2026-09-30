package app.bharatphoton.service.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import androidx.core.view.isVisible
import app.bharatphoton.service.api_model_classes.CmsResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityTermsAndConditionsBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.AboutUsActivity
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.CMS_TERMS_CONDITIONS
import app.bharatphoton.service.utils.CrashlyticsConstants.OTP_VERIFICATION
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.GlideImageGetter
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class TermsAndConditionsActivity : BaseActivity() {
    private lateinit var binding: ActivityTermsAndConditionsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(CMS_TERMS_CONDITIONS,"TermsAndConditionsActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        termsConditionApiCall()

        binding.ivNotification.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("NAVIGATE_TO", "NOTIFICATION")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }

        applySlideInAnimation(binding.clTerms)

    }

    private fun termsConditionApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.cms(token, "termsAndConditions")
        getDetail.enqueue(object : Callback<CmsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CmsResponse>, response: Response<CmsResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        val responseData = response.body()?.data
                        if (responseData != null) {
                            for (description in responseData){
                                val content = description?.descriptions.toString().trim()
                                val imageGetter = GlideImageGetter(this@TermsAndConditionsActivity, binding.tvTerms)
                                val styledText = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
                                binding.tvTerms.text = styledText
                                binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
                                Linkify.addLinks(binding.tvTerms, Linkify.ALL)
                                binding.tvTerms.setLinkTextColor(Color.BLUE)

                                binding.tvTerms.post {
                                    val layout = binding.tvTerms.layout
                                    if (layout != null && layout.lineCount > 10) {
                                        binding.nsvTerms.isScrollable = false

                                        binding.tvMore.visibility = View.VISIBLE
                                        binding.tvMore.setOnClickWithAnimation {
                                            binding.tvTerms.maxLines = Integer.MAX_VALUE
                                            binding.tvMore.visibility = View.GONE
                                            binding.nsvTerms.isScrollable = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@TermsAndConditionsActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("termsConditionApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<CmsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@TermsAndConditionsActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("termsConditionApiCall-API call failed ${e.message}")
                }
            }
        })
    }
}