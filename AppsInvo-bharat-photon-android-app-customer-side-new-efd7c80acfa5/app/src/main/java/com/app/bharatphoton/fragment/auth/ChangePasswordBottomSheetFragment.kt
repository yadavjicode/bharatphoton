package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentChangePasswordBottomSheetBinding
import com.app.bharatphoton.module.authResponse.ChangePasswordResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordBottomSheetFragment : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentChangePasswordBottomSheetBinding.inflate(layoutInflater) }
    private val viewModule by viewModels<AuthViewModule>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiChangePasswordResponse()
    }

    private fun onClick() {
        with(binding) {
            saveBtn.setOnClickListener {
                if (validation()) {
                    apiChangePasswordRequest()
                }
            }
        }
    }

    private fun apiChangePasswordRequest() {
        val required = JsonObject().apply {
            addProperty("oldPassword", binding.oldPassword.text.toString())
            addProperty("password", binding.newPassword.text.toString())
        }
        viewModule.changePassword(required)
    }

    private fun apiChangePasswordResponse() {
        viewModule.changePassword.observe(viewLifecycleOwner) {
            if (it == null){
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    binding.saveBtn.hideLoading()
                }

                is NetworkResult.Loading -> binding.saveBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.saveBtn.hideLoading()
                    try {
                        changePasswordResponse = Gson().fromJson(it.data, ChangePasswordResponse::class.java)
                        if (changePasswordResponse?.code == 1) {
                            dismiss()
                        } else {
                            requireActivity().toast(changePasswordResponse?.message.toString())
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

        }
    }

    private fun validation(): Boolean {
        with(binding) {
            if (oldPassword.text.toString().isBlank()) {
                oldPassword.error = getString(R.string.please_enter_old_password)
                return false
            } else if (newPassword.text.toString().isBlank()) {
                newPassword.error = getString(R.string.please_enter_new_password)
                return false
            } else if (newPassword.text.toString() == oldPassword.text.toString()) {
                newPassword.error = getString(R.string.new_password_should_not_be_same_as_old_password)
                return false
            }
        }
        return true
    }

    companion object {
        private var changePasswordResponse: ChangePasswordResponse? = null
    }

}