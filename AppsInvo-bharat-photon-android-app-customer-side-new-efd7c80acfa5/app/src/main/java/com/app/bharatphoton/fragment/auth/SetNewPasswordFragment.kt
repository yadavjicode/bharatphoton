package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSetNewPasswordBinding
import com.app.bharatphoton.module.authResponse.SetNewPasswordResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SetNewPasswordFragment : BaseFragment<FragmentSetNewPasswordBinding>() {
    private val authViewModule by viewModels<AuthViewModule>()
    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSetNewPasswordBinding = FragmentSetNewPasswordBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        apiSetNewPasswordResponse()
    }

    private fun apiSetNewPasswordRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("password", confirmPassword.text.toString())
            }
            authViewModule.resetPassword(request)
        }
    }

    private fun validate(): Boolean {
        with(binding) {
            if (newPassword.text.toString().trim().isBlank()) {
                newPassword.apply {
                    error = getString(R.string.please_enter_new_password)
                    requestFocus()
                }
                return false
            } else if (confirmPassword.text.toString().trim().isBlank()) {
                confirmPassword.apply {
                    error = context.getString(R.string.please_enter_confirm_password)
                    requestFocus()
                }
                return false
            } else if (newPassword.text.toString() != confirmPassword.text.toString()) {
                confirmPassword.apply {
                    error = context.getString(R.string.password_not_match)
                    requestFocus()
                }
                return false
            }
        }
        return true
    }

    private fun apiSetNewPasswordResponse() {
        authViewModule.resetPassword.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.saveBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.saveBtn.hideLoading()
                    try {
                        setNewPasswordResponse = Gson().fromJson(it.data, SetNewPasswordResponse::class.java)
                        if (setNewPasswordResponse?.code == 1) {
                            navigateToScreen(R.id.loginFragment, null, navOptions)
                        } else {
                            requireActivity().toast(setNewPasswordResponse?.message.toString())
                        }

                    } catch (e: Exception) {
                        //
                    }
                }

                is NetworkResult.Error -> {
                    binding.saveBtn.hideLoading()
                    val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                    requireActivity().toast(errorMessage)
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            back.setOnAnimatedClickListener { navigateBack() }
            viewLifecycleOwner.handleOnPressBackButton { navigateBack() }
        }
    }

    private fun setClick() {
        with(binding) {
            saveBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiSetNewPasswordRequest()
                }
            }
        }
    }

    companion object {
        private var setNewPasswordResponse: SetNewPasswordResponse? = null
    }
}