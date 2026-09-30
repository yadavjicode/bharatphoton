package com.app.bharatphoton.fragment.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentUserManualBinding


class UserManualFragment : BaseFragment<FragmentUserManualBinding>() {
    private var userManual = ""
    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserManualBinding = FragmentUserManualBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, null, navOptions)
            }
            downloadImg.setOnAnimatedClickListener {
                Utils.downloadFile(context = requireContext(), url = userManual, fileName = userManual.substringAfterLast("/"))
            }
        }
    }

    private fun initData() {
        with(binding) {
            handleOnPressBackButton { navigateBack() }
            topBar.title.text = getString(R.string.user_manual)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            userManual = arguments?.getString("url", "") ?: ""

            showLoader()

            if (userManual.isNotEmpty()) {
                webView.apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            showLoader()
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            hideLoader()
                        }

                        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                            super.onReceivedError(view, request, error)
                            hideLoader()
                        }
                    }
                    val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$userManual"
                    loadUrl(googleDocsUrl)
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                }
            } else {
                requireActivity().toast("Invalid Url")
            }

        }
    }
}