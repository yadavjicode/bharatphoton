package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentTermsAndConditionsBinding
import com.app.bharatphoton.module.authResponse.CmsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionsFragment : BaseFragment<FragmentTermsAndConditionsBinding>() {
    private val viewModel by viewModels<AuthViewModule>()

    private val navOptions by lazy {
        Utils.navigateWithAnimation(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiCmsRequest()
        apiCmsResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentTermsAndConditionsBinding = FragmentTermsAndConditionsBinding.inflate(layoutInflater)

    private fun initData() {
        with(binding) {
            topBar.title.text = getString(R.string.terms_and_conditions)
            topBar.topBarFunctionality.visibility = View.GONE
            handleOnPressBackButton { navigateBack() }
            RecyclerAnimationHelper.animation(requireContext(),root)
        }
    }

    private fun apiCmsRequest() {
        viewModel.cmsData(1, "termsAndConditions")
    }

    private fun apiCmsResponse() {
        viewModel.cmsData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> hideLoader()
                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        cmsResponse = Gson().fromJson(it.data, CmsResponse::class.java)
                        val data = cmsResponse?.data
                        if (cmsResponse?.code == 1) {
                            data?.descriptions?.let {
                                binding.description.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                                binding.description.movementMethod = LinkMovementMethod.getInstance()
                                binding.description.linksClickable = true
                            }

                        } else {
                            requireActivity().toast(cmsResponse?.message.toString())
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
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }

    companion object {
        private var cmsResponse: CmsResponse? = null
    }

}