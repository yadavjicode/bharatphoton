package app.bharatphoton.service.ui.activities

import android.annotation.SuppressLint
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
import app.bharatphoton.service.databinding.ActivityAboutUsBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.CMS_ABOUT_US
import app.bharatphoton.service.utils.CrashlyticsConstants.CMS_PRIVACY_POLICY
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

class AboutUsActivity : BaseActivity() {
    private lateinit var binding: ActivityAboutUsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(CMS_ABOUT_US,"AboutUsActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        aboutUsApiCall()

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }

        applySlideInAnimation(binding.clAboutUs)

    }

    private fun aboutUsApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.cms(token, "aboutUs")
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
                                val htmlContent = description?.descriptions.orEmpty().trim()
                                val imageGetter = GlideImageGetter(this@AboutUsActivity, binding.tvAboutUs)
                                val styledText = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY, imageGetter, null)
                                binding.tvAboutUs.text = styledText
                                binding.tvAboutUs.movementMethod = LinkMovementMethod.getInstance()
                                Linkify.addLinks(binding.tvAboutUs, Linkify.ALL)

                                binding.tvAboutUs.post {
                                    val layout = binding.tvAboutUs.layout
                                    if (layout != null && layout.lineCount > 20) {
                                        binding.nsvAboutUs.isScrollable = false

                                        binding.tvMore.visibility = View.VISIBLE
                                        binding.tvMore.setOnClickWithAnimation {
                                            binding.tvAboutUs.maxLines = Integer.MAX_VALUE
                                            binding.tvMore.visibility = View.GONE
                                            binding.nsvAboutUs.isScrollable = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@AboutUsActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("aboutUsApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<CmsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@AboutUsActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("aboutUsApiCall-API call failed ${e.message}")
                }
            }
        })
    }
}