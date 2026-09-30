package com.app.bharatphoton.fragment.auth

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant.SharedPref.AUTH_TOKEN
import com.app.bharatphoton.common.Constant.SharedPref.PROFILE_NAME
import com.app.bharatphoton.common.Constant.SharedPref.PROFILE_PIC
import com.app.bharatphoton.common.SharedViewModule
import com.app.bharatphoton.common.Utils.hideKeyboard
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentOTPBinding
import com.app.bharatphoton.module.authResponse.SendOtpResponse
import com.app.bharatphoton.module.authResponse.VerifyOtpResponse
import com.app.bharatphoton.module.profileResponse.VerifyPhoneEmailResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.app.bharatphoton.viewModule.ProfileViewModule
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.otpview.OTPListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OTPFragment : BaseFragment<FragmentOTPBinding>() {

    private lateinit var sharedViewModule: SharedViewModule
    private var email = ""
    private var emailOrPhone = "phone"
    private var notificationType = "phone"
    private var authToken = ""
    private var phone = ""
    private var requestType = ""
    private var type = ""
    private var user_type = ""
    private val authViewModule by viewModels<AuthViewModule>()
    private val navOption by lazy { navigateWithAnimation(requireContext()) }
    private val viewModule by viewModels<ProfileViewModule>()
    private val otpInput = StringBuilder()

    private val timer = object : CountDownTimer(60000, 1000) {
        @SuppressLint("DefaultLocale")
        override fun onTick(p0: Long) {
            val seconds = (p0 / 1000)
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            binding.time.text = String.format("%02d:%02d Sec", minutes, remainingSeconds)
        }

        @SuppressLint("SetTextI18n")
        override fun onFinish() {
            binding.resendCode.visibility = View.VISIBLE
            binding.time.text = "00:00 Sec"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        authViewModule.resendOtp.postValue(null)
        authViewModule.verifyOtp.postValue(null)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModule = ViewModelProvider(requireActivity())[SharedViewModule::class.java]
        initData()
        setClick()
        automaticNavigateNextScreen()
        apiVerifyResponse()
        apiSendOtpResponse()
        apiVerifyEmailPhoneResponse()

    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentOTPBinding = FragmentOTPBinding.inflate(layoutInflater)

    private fun apiSendOtpRequest() {
        with(binding) {
            "user_type -> $user_type".mlog()
            "phone -> $phone".mlog()
            val request = JsonObject().apply {
                if (user_type == "business") {
                    if (phone.isNotBlank() && phone != "null") {
                        addProperty("phoneNo", phone)
                        addProperty("userType", 2)
                    } else {
                        addProperty("email", email)
                        addProperty("userType", 2)
                    }
                } else {
                    if (phone.isNotBlank()) {
                        addProperty("phoneNo", phone)
                        addProperty("userType", 1)
                    } else {
                        addProperty("email", email)
                        addProperty("userType", 2)
                    }
                }
            }
            authViewModule.resendOtp(request)
        }
    }

    private fun apiSendOtpResponse() {
        authViewModule.resendOtp.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        sendOtpResponse = Gson().fromJson(it.data.toString(), SendOtpResponse::class.java)
                        if (sendOtpResponse?.code == 1) {
                            timer.start()
                            binding.resendCode.visibility = View.GONE

                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiVerifyEmailPhoneRequest() {
        val request = JsonObject().apply {
            if (requestType == "email") {
                addProperty("email_otp", binding.OtpView.otp)
            } else {
                addProperty("phone_otp", binding.OtpView.otp)
            }
        }
        viewModule.verifyEmailPhone(request)
    }

    private fun apiVerifyEmailPhoneResponse() {
        viewModule.verifyEmailPhone.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.verifyBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.verifyBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.verifyBtn.hideLoading()
                    try {
                        verifyPhoneEmailResponse = Gson().fromJson(it.data, VerifyPhoneEmailResponse::class.java)
                        if (verifyPhoneEmailResponse?.code == 1) {

                            sharedPrefManager.saveString("phoneNumber", verifyPhoneEmailResponse?.data?.phoneNo ?: "")
                            navigateBack()
                        } else {
                            requireActivity().toast(verifyPhoneEmailResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiVerifyOtpRequest(type: Int) {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("type", type)
                when (emailOrPhone) {
                    "email" -> addProperty("email_otp", OtpView.otp)
                    "phone" -> addProperty("phone_otp", OtpView.otp)
                    else -> addProperty("phone_otp", OtpView.otp)
                }
            }
            authViewModule.verifyOtp(authToken, request = request)
        }
    }

    private fun apiVerifyResponse() {
        authViewModule.verifyOtp.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.verifyBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.verifyBtn.hideLoading()
                    try {
                        verifyOtpResponse = Gson().fromJson(it.data.toString(), VerifyOtpResponse::class.java)
                        sharedPrefManager.saveString(PROFILE_PIC, verifyOtpResponse?.data?.profileImage ?: "")
                        sharedPrefManager.saveString(PROFILE_NAME, verifyOtpResponse?.data?.firstName ?: "")
                        sharedPrefManager.saveString("phoneNumber", verifyOtpResponse?.data?.phoneNo ?: "")
                        sharedViewModule._profilePicUrl.value = verifyOtpResponse?.data?.profileImage ?: ""
                        sharedPrefManager.saveString(AUTH_TOKEN, verifyOtpResponse?.data?.token ?: "")
                        if (verifyOtpResponse?.code == 1) {
                            if (type == "forgetPassword") {
                                navigateToScreen(R.id.setNewPasswordFragment, null, navOption)
                            } else {
                                navigateToScreen(R.id.homeFragment, null, navOption)
                            }
                        } else {
                            requireActivity().toast(verifyOtpResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    binding.verifyBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun automaticNavigateNextScreen() {
        binding.OtpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                if (validate()) {
                    if (type == "Change") {
                        apiVerifyEmailPhoneRequest()
                        hideKeyboard(requireContext(), requireView())
                    } else {
                        when (notificationType) {
                            "login" -> {
                                apiVerifyOtpRequest(1)
                                hideKeyboard(requireContext(), requireView())
                            }

                            "forgetPassword" -> {
                                apiVerifyOtpRequest(2)
                                hideKeyboard(requireContext(), requireView())
                            }

                            "register" -> {
                                apiVerifyOtpRequest(1)
                                hideKeyboard(requireContext(), requireView())
                            }
                            else -> {
                                //
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initData() {
        timer.start()
        email = arguments?.getString("email","")?:""
        phone = arguments?.getString("phone","")?:""
        type = arguments?.getString("type").toString()
        user_type = arguments?.getString("userType").toString()
        notificationType = arguments?.getString("notificationType").toString()
        requestType = arguments?.getString("requestType").toString()
        emailOrPhone = arguments?.getString("emailOrPhone").toString()
        authToken = arguments?.getString("authToken").toString()

        with(binding) {
            resendCode.visibility = View.GONE
            if (notificationType == "register") {
                otpTypeMsgTv.text = getString(R.string.please_enter_6_digits_code_which_has_been_nsent_to_your_registered_email)
            } else {
                otpTypeMsgTv.text = getString(R.string.please_enter_6_digits_code_which_has_been_nsent_to_your_registered_mobile_number)
            }
            when (emailOrPhone) {
                "email" -> otpTypeMsgTv.text = getString(R.string.please_enter_6_digits_code_which_has_been_nsent_to_your_registered_email)
                "phone" -> otpTypeMsgTv.text = getString(R.string.please_enter_6_digits_code_which_has_been_nsent_to_your_registered_mobile_number)
                else -> {
                    //
                }
            }
        }
    }

    private fun setClick() {
        with(binding) {
            resendCode.setOnAnimatedClickListener {
                apiSendOtpRequest()
            }

            verifyBtn.setOnAnimatedClickListener {
                if (validate()) {
                    if (type == "change") {
                        apiVerifyEmailPhoneRequest()
                    } else {
                        when (notificationType) {
                            "login" -> {
                                apiVerifyOtpRequest(1)
                            }

                            "forgetPassword" -> {
                                apiVerifyOtpRequest(2)
                            }

                            "register" -> {
                                apiVerifyOtpRequest(1)
                            }
                            else -> {
                                //
                            }
                        }
                    }
                }
            }

            back.setOnAnimatedClickListener {
                navigateBack()
            }
            viewLifecycleOwner.handleOnPressBackButton {
                navigateBack()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(requireContext())
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Toast.makeText(requireContext(), "Waiting for OTP...", Toast.LENGTH_SHORT).show()
        }
        task.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to start SMS retriever.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): Boolean {
        with(binding) {
            if (OtpView.otp.toString().isBlank()) {
                requireActivity().toast(getString(R.string.please_enter_an_otp))
                return false
            } else if (OtpView.otp.toString().length < 6) {
                requireActivity().toast(getString(R.string.please_enter_valid_otp))
                return false
            }
        }
        return true
    }

    companion object {
        private var verifyOtpResponse: VerifyOtpResponse? = null
        private var verifyPhoneEmailResponse: VerifyPhoneEmailResponse? = null
        private var sendOtpResponse: SendOtpResponse? = null
    }

}