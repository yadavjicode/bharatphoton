@file:Suppress("DEPRECATION")

package com.app.bharatphoton.fragment.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.bharatphoton.R
import com.app.bharatphoton.common.SharedPrefManager
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentDeleteAccountBottomSheetBinding
import com.app.bharatphoton.module.authResponse.DeleteAccountResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.github.ybq.android.spinkit.style.Circle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DeleteAccountBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var loader: ProgressDialog

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    private val binding by lazy {
        FragmentDeleteAccountBottomSheetBinding.inflate(layoutInflater)
    }

    private val navOptions by lazy {
        Utils.navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<AuthViewModule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        initLoader()
        apiDeleteAccountResponse()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }

    fun showLoader() {
        if (!loader.isShowing) {
            loader.show()
        }
    }

    private fun apiDeleteAccountRequest() {
        val request = JsonObject().apply {
            addProperty("isDeleted", true)
        }
        viewModule.deleteAccount(request)
    }

    private fun apiDeleteAccountResponse() {
        viewModule.deleteAccount.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        deleteAccountResponse = Gson().fromJson(it.data, DeleteAccountResponse::class.java)
                        if (deleteAccountResponse?.code == 1) {
                            dismiss()
                            sharedPrefManager.removeAll()

                            findNavController().navigate(R.id.loginFragment, null, navOptions = navOptions)
                        } else {
                            requireActivity().toast(deleteAccountResponse?.message ?: "")
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

    private fun initLoader() {
        val loaderStyle = Circle().apply {
            color = resources.getColor(R.color.Deep_Saffron)
        }
        loader = ProgressDialog(requireContext(), R.style.TransparentProgressDialog).apply {
            setCancelable(true)
            setIndeterminateDrawable(loaderStyle)
        }
    }

    private fun onClick() {
        with(binding) {
            noGoBackBtn.setOnAnimatedClickListener {
                dismiss()
            }
            yesDeleteBtn.setOnAnimatedClickListener {
                apiDeleteAccountRequest()
            }
        }
    }

    companion object {
        private var deleteAccountResponse: DeleteAccountResponse? = null
    }

}