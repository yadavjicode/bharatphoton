package com.app.bharatphoton.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentEnquireNowDialogBinding
import com.app.bharatphoton.module.authResponse.AddQuerryResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class EnquireNowDialogFragment : DialogFragment() {

    private var categoryId = ""
    private val binding by lazy { FragmentEnquireNowDialogBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<AuthViewModule>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//        dialog?.window?.setBackgroundDrawableResource(R.drawable._style_rounded_corners)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiAddQueryResponse()
    }

    private fun apiAddQueryRequest() {
        val request = JsonObject().apply {
            addProperty("fullName", binding.firstName.text.toString())
            addProperty("email", binding.emailAddress.text.toString())
            addProperty("phoneNo", binding.phoneNumberEt.text.toString())
            addProperty("countryCode", binding.ccp.selectedCountryCodeWithPlus)
            addProperty("categoryId", categoryId)
        }
        viewModel.addQuery(request)
    }

    private fun apiAddQueryResponse() {
        viewModel.addQuery.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error<*> -> {
                    binding.submitBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject().optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading<*> -> binding.submitBtn.showLoading()
                is NetworkResult.Success<*> -> {
                    binding.submitBtn.hideLoading()
                    try {
                        addQueryResponse = Gson().fromJson(it.data, AddQuerryResponse::class.java)
                        if (addQueryResponse?.code == 1) {
                            requireActivity().toast("Thank you, your request has been submitted successfully.")
                            dismiss()
                        } else {
                            requireActivity().toast(addQueryResponse?.message.toString())
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

    private fun initData() {
        with(binding) {
            arguments?.getString("categoryName", "") ?: "".mlog()
            productTv.text = arguments?.getString("categoryName", "") ?: ""
            categoryId = arguments?.getString("categoryId", "") ?: ""
        }
    }

    private fun onClick() {
        with(binding) {
            closeBtn.setOnAnimatedClickListener {
                dismiss()
            }
            submitBtn.setOnAnimatedClickListener {
                if (validation()) {
                    apiAddQueryRequest()
                }
            }
        }
    }

    private fun validation(): Boolean {
        with(binding) {
            if (firstName.text.toString().isEmpty()) {
                firstName.error = getString(R.string.enter_your_name)
                firstName.requestFocus()
                return false
            } else if (emailAddress.text.toString().isEmpty()) {
                emailAddress.error = getString(R.string.enter_your_email)
                emailAddress.requestFocus()
                return false
            } else if (!emailAddress.text.toString().trim().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                emailAddress.apply {
                    error = getString(R.string.please_enter_valid_email)
                    requestFocus()
                    return false
                }
            } else if (phoneNumberEt.text.toString().isEmpty()) {
                phoneNumberEt.error = getString(R.string.please_enter_phone_number)
                phoneNumberEt.requestFocus()
                return false
            } else if (phoneNumberEt.text.toString().length < 10) {
                phoneNumberEt.apply {
                    error = getString(R.string.please_enter_valid_phone_number)
                    requestFocus()
                    return false
                }
            } else if (productTv.text.toString().isEmpty()) {
                productTv.error = getString(R.string.select_product_interest)
                return false
            } else {
                //
            }
        }
        return true
    }

    companion object {
        private var addQueryResponse: AddQuerryResponse? = null
    }

}