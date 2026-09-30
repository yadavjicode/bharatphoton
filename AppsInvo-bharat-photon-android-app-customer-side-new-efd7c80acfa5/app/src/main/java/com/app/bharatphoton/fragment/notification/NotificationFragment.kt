package com.app.bharatphoton.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.LoaderAdapter
import com.app.bharatphoton.adapter.NotificationAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.navigateBackScreen
import com.app.bharatphoton.common.SharedData.notificationCount
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentNotificationBinding
import com.app.bharatphoton.viewModule.NotificationViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val viewModule by viewModels<NotificationViewModule>()
    private var isFirstLoad = true

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val notificationAdapter by lazy {
        NotificationAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiNotificationListRequest("")
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentNotificationBinding = FragmentNotificationBinding.inflate(layoutInflater)

    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.notifications)
            notificationCount = 0
            refresh { onComplete ->
                apiNotificationListRequest("")
                onComplete()
            }
            handleOnPressBackButton {
                navigateBackScreen = ""
                navigateBack()
            }
        }
    }

    private fun apiNotificationListRequest(read: String) {
        lifecycleScope.launch {
            viewModule.notificationList(read = read).collectLatest { value ->
                notificationAdapter.submitData(value)
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBackScreen = ""
                navigateBack()
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            notificationRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = notificationAdapter.withLoadStateFooter(LoaderAdapter())

            }
            notificationAdapter.addLoadStateListener { loadState ->
                when (loadState.refresh) {
                    is LoadState.Error -> hideLoader()
                    LoadState.Loading -> showLoader()
                    is LoadState.NotLoading -> {
                        hideLoader()
                        if (isFirstLoad) {
                            applyRecyclerAnimation(requireContext(), notificationRv)
                            isFirstLoad = false
                        }
                        if (notificationAdapter.itemCount == 0) {
                            notFound.visibility = View.VISIBLE
                        } else {
                            notFound.visibility = View.GONE
                        }
                    }
                    else -> {
                        //
                    }
                }
            }
        }
    }
}