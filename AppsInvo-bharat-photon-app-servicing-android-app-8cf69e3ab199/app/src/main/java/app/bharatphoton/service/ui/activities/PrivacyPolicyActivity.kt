package app.bharatphoton.service.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import androidx.core.view.isVisible
import app.bharatphoton.service.api_model_classes.CmsResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityPrivacyPolicyBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.TermsAndConditionsActivity
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.CMS_PRIVACY_POLICY
import app.bharatphoton.service.utils.CrashlyticsConstants.CMS_TERMS_CONDITIONS
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

class PrivacyPolicyActivity : BaseActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(CMS_PRIVACY_POLICY,"PrivacyPolicyActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        privacyAndPolicyApiCall()

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

        applySlideInAnimation(binding.clPrivacy)

    }

    private fun privacyAndPolicyApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.cms(token, "privacyAndPolicy")
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
                                val imageGetter = GlideImageGetter(this@PrivacyPolicyActivity, binding.tvPrivacyPolicy)
                                val styledText = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
                                binding.tvPrivacyPolicy.text = styledText
                                binding.tvPrivacyPolicy.movementMethod = LinkMovementMethod.getInstance()
                                Linkify.addLinks(binding.tvPrivacyPolicy, Linkify.ALL)
                                binding.tvPrivacyPolicy.setLinkTextColor(Color.BLUE)

                                binding.tvPrivacyPolicy.post {
                                    val layout = binding.tvPrivacyPolicy.layout
                                    if (layout != null && layout.lineCount > 10) {
                                        binding.nsvPrivacyPolicy.isScrollable = false

                                        binding.tvMore.visibility = View.VISIBLE
                                        binding.tvMore.setOnClickWithAnimation {
                                            binding.tvPrivacyPolicy.maxLines = Integer.MAX_VALUE
                                            binding.tvMore.visibility = View.GONE
                                            binding.nsvPrivacyPolicy.isScrollable = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@PrivacyPolicyActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("privacyAndPolicyApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<CmsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@PrivacyPolicyActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("privacyAndPolicyApiCall-API call failed ${e.message}")
                }
            }
        })
    }
}

