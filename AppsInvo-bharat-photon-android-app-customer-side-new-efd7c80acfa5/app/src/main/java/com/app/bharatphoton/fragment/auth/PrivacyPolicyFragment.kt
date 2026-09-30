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
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.ImageGetter
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentPrivacyPolicyBinding
import com.app.bharatphoton.module.authResponse.CmsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding>() {

    private val viewModel by viewModels<AuthViewModule>()

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
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
    ): FragmentPrivacyPolicyBinding = FragmentPrivacyPolicyBinding.inflate(layoutInflater)

    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.title.text = getString(R.string.privacy_policy)
            topBar.topBarFunctionality.visibility = View.GONE
            handleOnPressBackButton { navigateBack() }
            RecyclerAnimationHelper.animation(requireContext(), root)
        }
    }

    private fun apiCmsRequest() {
        viewModel.cmsData(1, "privacyAndPolicy")
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
                            data?.descriptions?.let { htmlText ->
                                binding.description.apply {
                                    text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY, ImageGetter(binding.description), null)
                                    movementMethod = LinkMovementMethod.getInstance()
                                }
                            }
                        } else {
                            requireActivity().toast(cmsResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        //
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
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
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