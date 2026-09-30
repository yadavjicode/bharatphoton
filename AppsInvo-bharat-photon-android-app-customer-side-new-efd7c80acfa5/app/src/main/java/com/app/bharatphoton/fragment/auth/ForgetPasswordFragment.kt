package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentForgetPasswordBinding
import com.app.bharatphoton.module.authResponse.ForgetPasswordResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ForgetPasswordFragment : BaseFragment<FragmentForgetPasswordBinding>() {

    private val authViewModule by viewModels<AuthViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModule.forgetPassword.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        emailPhoneEditText()
        apiForgetPasswordResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgetPasswordBinding = FragmentForgetPasswordBinding.inflate(layoutInflater)

    private fun apiForgetPasswordRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("userType", 2)
                if (emailOrPhone.text.any { it.isLetter() }) {
                    addProperty("email", emailOrPhone.text.toString())
                } else {
                    addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                    addProperty("phoneNo", emailOrPhone.text.toString())
                }
            }
            authViewModule.forgetPassword(request)
        }
    }

    private fun apiForgetPasswordResponse() {
        authViewModule.forgetPassword.observe(viewLifecycleOwner) {
            if (it == null){
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Loading -> {
                    binding.continueBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.continueBtn.hideLoading()
                    try {
                        forgetPasswordResponse = Gson().fromJson(it.data, ForgetPasswordResponse::class.java)
                        if (forgetPasswordResponse?.code == 1) {
//                            sharedPrefManager.saveString(Constants.SharedPref.AUTH_TOKEN, forgetPasswordResponse?.data.toString())
                            val bundle = Bundle().apply {
                                putString("type", "forgetPassword")
                                putString("notificationType", "forgetPassword")
                                putString("userType", "business")
                                putString("authToken", forgetPasswordResponse?.data)

                                if (binding.emailOrPhone.text.contains(".")) {
                                    putString("emailOrPhone", "email")
                                    putString("email", binding.emailOrPhone.text.toString())
                                } else {
                                    putString("emailOrPhone", "phone")
                                    putString("phone", binding.emailOrPhone.text.toString())
                                }
                            }
                            navigateToScreen(R.id.OTPFragment, bundle, navOptions)
                        } else {
                            requireActivity().toast(forgetPasswordResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    binding.continueBtn.hideLoading()
                    try {
                        val message = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(message)
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

    private fun emailPhoneEditText() {
        with(binding) {
            emailOrPhone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            emailOrPhone.filters = arrayOf(InputFilter.LengthFilter(50))
            ccp.visibility = View.GONE

            emailOrPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val inputText = s.toString()

                    if (inputText.isBlank() || inputText.any { it.isLetter() }) {
                        ccp.visibility = View.GONE
                        emailOrPhone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        emailOrPhone.filters = arrayOf(InputFilter.LengthFilter(50))
                    } else {
                        ccp.visibility = View.VISIBLE
                        emailOrPhone.inputType = InputType.TYPE_CLASS_PHONE
                        emailOrPhone.filters = arrayOf(InputFilter.LengthFilter(10))
                    }
                    emailOrPhone.setSelection(emailOrPhone.text.length)
                }

                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        val inputText = it.toString()
                        val maxLength = if (ccp.visibility == View.VISIBLE) 10 else 50

                        if (inputText.length > maxLength) {
                            emailOrPhone.setText(inputText.substring(0, maxLength))
                            emailOrPhone.setSelection(maxLength)
                        }
                    }
                }
            })
        }
    }


    private fun initData() {
        with(binding) {
            emailOrPhone.addTextChangedListener {

            }
        }
    }

    private fun setClick() {
        with(binding) {
            back.setOnAnimatedClickListener { navigateBack() }
            continueBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiForgetPasswordRequest()
                }
            }
            viewLifecycleOwner.handleOnPressBackButton { navigateBack() }
        }
    }

    private fun validate(): Boolean {
        val input = binding.emailOrPhone.text.toString().trim()

        if (input.isBlank()) {
            binding.emailOrPhone.apply {
                error = context.getString(R.string.please_enter_email_or_phone_number)
                requestFocus()
            }
            return false
        }

        return if (input.any { it.isLetter() }) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                binding.emailOrPhone.apply {
                    error = getString(R.string.please_enter_valid_email)
                    requestFocus()
                }
                false
            } else {
                true
            }
        } else {
            if (input.length < 7 || input.length > 10) {
                binding.emailOrPhone.apply {
                    error = getString(R.string.please_enter_valid_phone_number)
                    requestFocus()
                }
                false
            } else {
                true
            }
        }
    }

    companion object {
        private var forgetPasswordResponse: ForgetPasswordResponse? = null
    }
}