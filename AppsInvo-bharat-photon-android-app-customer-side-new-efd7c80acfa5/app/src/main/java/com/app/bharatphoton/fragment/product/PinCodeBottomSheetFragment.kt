package com.app.bharatphoton.fragment.product

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.hideKeyBoardWithEditTextLength
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.FragmentPinCodeBottomSheetBinding
import com.app.bharatphoton.module.subcriptionResponse.CheckSubscriptionResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.SubscriptionViewModule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinCodeBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModule by viewModels<SubscriptionViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private var productId = ""
    private val binding by lazy {
        FragmentPinCodeBottomSheetBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiCheckSubscriptionResponse()
    }

    private fun apiCheckSubscriptionRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("productId", productId)
                addProperty("pincode", etPinCode.text.toString().trim())
            }
            viewModule.checkSubscription(request)
        }
    }

    private fun apiCheckSubscriptionResponse() {
        viewModule.checkSubscription.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> binding.checkBtn.hideLoading()
                is NetworkResult.Loading -> binding.checkBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.checkBtn.hideLoading()
                    try {
                        checkSubscriptionResponse = Gson().fromJson(it.data, CheckSubscriptionResponse::class.java)
                        if (checkSubscriptionResponse?.code == 1) {
                            dismiss()
                            val bundle = Bundle().apply {
                                putString("pinCode", binding.etPinCode.text.toString().trim())
                                putString("productId", productId)
                            }

                            findNavController().navigate(R.id.subscribeFragment, bundle, navOptions)
                        } else {
                            showDialog(context = requireContext(), messageText = getString(R.string.subscription_not_available_in_your_pincode))
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

    private fun onClick() {
        with(binding) {
            checkBtn.setOnAnimatedClickListener {
                if (etPinCode.text.toString().isNotEmpty()) {
                    apiCheckSubscriptionRequest()

                } else {
                    etPinCode.requestFocus()
                    etPinCode.error = getString(R.string.please_enter_pincode)
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    private fun initData() {
        with(binding) {
            productId = arguments?.getString("productId","") ?: ""
            hideKeyBoardWithEditTextLength(editText = etPinCode, context = requireContext(), view = requireView(), length = 6)
            etPinCode.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().length == 6) {
                        apiCheckSubscriptionRequest()
                    }
                }
            })
        }
    }

    companion object {
        private var checkSubscriptionResponse: CheckSubscriptionResponse? = null
    }
}