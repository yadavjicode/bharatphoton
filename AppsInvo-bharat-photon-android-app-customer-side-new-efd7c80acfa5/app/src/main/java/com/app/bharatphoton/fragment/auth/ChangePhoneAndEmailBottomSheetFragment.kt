package com.app.bharatphoton.fragment.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentChangePhoneAndEmailBinding
import com.app.bharatphoton.module.profileResponse.ChangeEmailPhoneNumberResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ProfileViewModule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ChangePhoneAndEmailBottomSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentChangePhoneAndEmailBinding.inflate(layoutInflater) }
    private var type = ""
    private val viewModule by viewModels<ProfileViewModule>()
    private val navOption by lazy { navigateWithAnimation(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiChangeEmailPhoneResponse()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun LifecycleOwner.handleOnPressBackButton(onBackPressed: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed.invoke() }
    }

    private fun apiChangeEmailPhoneRequest() {
        val request = JsonObject().apply {
            if (type == "phoneNumber") {
                addProperty("country_code", binding.ccp.selectedCountryCodeWithPlus)
                addProperty("phoneNo", binding.phoneNumberEt.text.toString())
            } else {
                addProperty("email", binding.emailAddress.text.toString())
            }
        }
        viewModule.changeEmailPhone(request)
    }

    private fun apiChangeEmailPhoneResponse() {
        viewModule.changeEmailPhone.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.done.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.done.showLoading()
                is NetworkResult.Success -> {
                    binding.done.hideLoading()
                    try {
                        changeEmailPhoneNumberResponse = Gson().fromJson(it.data, ChangeEmailPhoneNumberResponse::class.java)
                        if (changeEmailPhoneNumberResponse?.code == 1) {
                            dismiss()
                            val bundle = Bundle().apply {
                                putString("type", "Change")

                                if (type == "phoneNumber") {
                                    putString("requestType", "phone")
                                    putString("emailOrPhone", "phone")
                                } else {
                                    putString("requestType", "email")
                                    putString("emailOrPhone", "email")
                                }
                            }
                            findNavController().navigate(R.id.OTPFragment, bundle, navOption)
                        } else {
                            requireActivity().toast(changeEmailPhoneNumberResponse?.message.toString())
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

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            handleOnPressBackButton { dismiss() }
            type = arguments?.getString("type", "") ?: ""
            emailAddress.setText(arguments?.getString("email", "") ?: "")
            phoneNumberEt.setText(arguments?.getString("phone", "") ?: "")
            if (type == "phoneNumber") {
                mobileNumberLayout.visibility = View.VISIBLE
                emailAddressLayout.visibility = View.GONE
                titleTv.text = getString(R.string.change_mobile_number)
                typeTv.text = getString(R.string.mobile)
            } else if (type == "emailAddress") {
                mobileNumberLayout.visibility = View.GONE
                emailAddressLayout.visibility = View.VISIBLE
                titleTv.text = getString(R.string.change_email_address)
                typeTv.text = getString(R.string.email)
            }
        }
    }

    private fun onClick() {
        with(binding) {
            done.setOnAnimatedClickListener {
                if (type == "phoneNumber") {
                    if (validate()) {
                        apiChangeEmailPhoneRequest()
                    }
                } else if (type == "emailAddress") {
                    if (emailValidation()) {
                        apiChangeEmailPhoneRequest()
                    }
                }
            }
        }
    }

    private fun emailValidation(): Boolean {
        with(binding) {
            if (emailAddress.text.toString().isBlank()) {
                emailAddress.error = getString(R.string.please_enter_email)
                return false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress.text.toString()).matches()) {
                emailAddress.error = getString(R.string.please_enter_valid_email)
                return false
            }
        }
        return true
    }

    private fun validate(): Boolean {
        with(binding) {
            if (phoneNumberEt.text.toString() == "") {
                phoneNumberEt.error = getString(R.string.please_enter_phone_number)
                return false
            } else if (phoneNumberEt.text.toString().length <= 4) {
                phoneNumberEt.error = getString(R.string.please_enter_valid_phone_number)
                return false
            }
        }
        return true
    }

    companion object {
        private var changeEmailPhoneNumberResponse: ChangeEmailPhoneNumberResponse? = null
    }
}