package com.app.bharatphoton.fragment.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.setupTapToCopy
import com.app.bharatphoton.databinding.FragmentReferFriendBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReferFriendFragment : BaseFragment<FragmentReferFriendBinding>() {

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        onClick()
        setupTapToCopy(textView = binding.tapToCotyTv, context = requireContext())
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentReferFriendBinding = FragmentReferFriendBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            RecyclerAnimationHelper.animation(context = requireContext(), views = root)
            topBar.title.text = getString(R.string.refer_a_friend)
            handleOnPressBackButton {
                navigateBack()
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
            shareClick(listOf(shareBtn))
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }

    private fun share(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun shareClick(view: List<View>) {
        view.forEach {
            it.setOnAnimatedClickListener {
                share("This is bharat photon application and yGg741 is your refer code")
            }
        }
    }

}