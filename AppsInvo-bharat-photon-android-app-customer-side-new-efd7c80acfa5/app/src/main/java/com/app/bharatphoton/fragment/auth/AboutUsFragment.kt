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
import com.app.bharatphoton.common.ImageGetterHelper
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentAboutUsBinding
import com.app.bharatphoton.module.authResponse.CmsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsFragment : BaseFragment<FragmentAboutUsBinding>() {

    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val viewModel by viewModels<AuthViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiCmsRequest()
        apiCmsResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAboutUsBinding = FragmentAboutUsBinding.inflate(layoutInflater)

    private fun apiCmsRequest() {
        viewModel.cmsData(1, "aboutUs")
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
                            binding.description.post {
                                binding.description.text = Html.fromHtml(
                                    data?.descriptions, Html.FROM_HTML_MODE_LEGACY, ImageGetterHelper.GlideImageGetter(binding.description), null
                                )
                                binding.description.movementMethod = LinkMovementMethod.getInstance()
                            }

                        } else {
                            requireActivity().toast(cmsResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.title.text = getString(R.string.about_us)
            topBar.topBarFunctionality.visibility = View.GONE
            binding.topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            handleOnPressBackButton { navigateBack() }

            RecyclerAnimationHelper.animation(context = requireContext(), views = root)
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
        }
    }

    companion object {
        private var cmsResponse: CmsResponse? = null
    }

}