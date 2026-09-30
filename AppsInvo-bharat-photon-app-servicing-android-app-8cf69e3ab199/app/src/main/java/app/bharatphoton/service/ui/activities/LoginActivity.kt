package app.bharatphoton.service.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.error401.Error401
import app.bharatphoton.service.api_model_classes.login.request.LoginRequest
import app.bharatphoton.service.api_model_classes.login.response.LoginResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityLoginBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.FULL_NAME
import app.bharatphoton.service.utils.Constants.IS_ALREADY_LOGGED_IN
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.NAME
import app.bharatphoton.service.utils.Constants.PHONE_NO
import app.bharatphoton.service.utils.Constants.PROFILE_PIC
import app.bharatphoton.service.utils.CrashlyticsConstants.LOGIN
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
import kotlin.toString

class LoginActivity : BaseActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(LOGIN,"LoginActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)

        getStorageDetailsApiCall(this)

        applySlideInAnimation(binding.clLogin)

        // Password show and hide
        var f =true
        binding.ivEyeIcon.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            if(f) {
                binding.etPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
                binding.ivEyeIcon.setImageResource(R.drawable.eye_close)
                f=false
            }
            else {
                binding.etPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                binding.ivEyeIcon.setImageResource(R.drawable.eye_open)
                f=true
            }
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        // TODO: REMOVE CODE IF NOT NEEDED
        /*binding.tvForgot.setOnClickWithAnimation {
            val intent = Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }*/

        binding.tvBtnLogin.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            if (binding.etEmail.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Email is required!")
            }
            else if (binding.etEmail.text.toString().isNotEmpty()
                && !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()
            ) {
                showAnimatedCustomToast("Please enter valid email.")
            }
            else if (binding.etPassword.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Password is required!")
            }
            else{
                loginApiCall()
            }
        }

        val editTexts = listOf(binding.etEmail, binding.etPassword)
        setCursorToEndOnFocus(editTexts)
    }

    private fun loginApiCall() {
        binding.clLoader.isVisible = true
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val requestBody = LoginRequest(email = email, password = password)
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.login(requestBody)
        getDetail.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
            {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                        val token = response.body()?.data?.auth_token.toString()
                        val firstName = response.body()?.data?.result?.firstName.toString()
                        val lastName = response.body()?.data?.result?.lastName.toString()
                        val profilePic = response.body()?.data?.result?.profileImage.toString()
                        val phoneNo = response.body()?.data?.result?.country_code + " " + response.body()?.data?.result?.phoneNo
                        sharedPreferencesHelper.saveString(LOGIN_TOKEN,token)
                        sharedPreferencesHelper.saveString(IS_ALREADY_LOGGED_IN,"True")
                        sharedPreferencesHelper.saveString(NAME,firstName)
                        sharedPreferencesHelper.saveString(FULL_NAME, "$firstName $lastName")
                        sharedPreferencesHelper.saveString(PROFILE_PIC,profilePic)
                        sharedPreferencesHelper.saveString(PHONE_NO,phoneNo)
                        val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                        startActivity(intent)
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
                        ApiErrorHandler.handleException(this@LoginActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("loginApiCall-Exception in parsing response ${e.message}.")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@LoginActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("loginApiCall-API call failed ${e.message}.")
                }
            }
        })
    }
}