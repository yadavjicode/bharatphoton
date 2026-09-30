@file:Suppress("KotlinConstantConditions")

package com.app.bharatphoton.fragment.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.LoaderAdapter
import com.app.bharatphoton.adapter.MyOrderAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentMyOrderBinding
import com.app.bharatphoton.viewModule.OrderViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyOrderFragment : BaseFragment<FragmentMyOrderBinding>() {

    private var isSelected = false
    private var isFirstLoad = true
    private var selectedIndex = 0

    private val myOrderAdapter by lazy {
        MyOrderAdapter { data, type, installationAddressId ->
            when (type) {
                "product" -> {
                    val bundle = Bundle().apply {
                        putInt("selectedIndex", selectedIndex)
                        putInt("orderStatus", data.products?.get(0)?.orderStatus ?: 0)
                        putString("orderId", data.id ?: "")
                        putString("productId", data.products?.get(0)?.id ?: "")
                    }
                    navigateToScreen(
                        id = R.id.orderDetailsShippedFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                "uploadSitePhoto" -> {
                    val bundle = Bundle().apply {
                        putString("orderNo", data.uuid)
                        putString("installationId", installationAddressId)
                        putString("orderId", data.id)
                    }

                    navigateToScreen(id = R.id.uploadPhotoFragment, bundle = bundle, navOptions = navOptions)
                }
                else -> {
                    //
                }
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<OrderViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        setDataInAdapter()
        handleOnPressBackButton { navigateBack() }
        optionLayout(defaultSelectedTab)
        when (defaultSelectedTab) {
            0 -> apiOrderListingRequest(4)
            1 -> apiOrderListingRequest(0)
            2 -> apiOrderListingRequest(1)
            3 -> apiOrderListingRequest(2)
            4 -> apiOrderListingRequest(3)
            else -> {
                //
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMyOrderBinding = FragmentMyOrderBinding.inflate(layoutInflater)

    private fun apiOrderListingRequest(status: Int) {
        viewModule.orderListing(status).observe(viewLifecycleOwner) { list ->
            lifecycleScope.launch {
                myOrderAdapter.submitData(list)
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            refresh { onComplete ->
                when (defaultSelectedTab) {
                    0 -> apiOrderListingRequest(4)
                    1 -> apiOrderListingRequest(0)
                    2 -> apiOrderListingRequest(1)
                    3 -> apiOrderListingRequest(2)
                    4 -> apiOrderListingRequest(3)
                }
                onComplete()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun optionLayout(selectedTab: Int) {
        with(binding) {
            optionLayout.forEachIndexed { index, view ->
                val cardView = view as CardView
                val textView = cardView.getChildAt(0) as TextView
                textView.text.toString()
                isSelected = index == selectedTab
                defaultSelectedTab = selectedTab

                cardView.setCardBackgroundColor(
                    if (isSelected) ContextCompat.getColor(
                        requireContext(), R.color.Deep_Saffron
                    ) else ContextCompat.getColor(requireContext(), R.color.white)
                )

                view.setOnAnimatedClickListener {
                    optionLayout(index)
                    selectedIndex = index
                    when (index) {
                        0 -> apiOrderListingRequest(4)
                        1 -> apiOrderListingRequest(0)
                        2 -> apiOrderListingRequest(1)
                        3 -> apiOrderListingRequest(2)
                        4 -> apiOrderListingRequest(3)
                    }
                }
            }
        }
    }

    private fun setClick() {
        with(binding) {
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInAdapter() {
        with(binding) {

            myOrderRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myOrderAdapter.withLoadStateFooter(LoaderAdapter())
            }

            myOrderAdapter.addLoadStateListener { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> showLoader()
                    is LoadState.NotLoading -> {
                        hideLoader()
                        if (isFirstLoad) {
                            RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), myOrderRv)
                            isFirstLoad = false
                        }
                        noFound.visibility = if (myOrderAdapter.itemCount == 0) View.VISIBLE else View.GONE
                    }

                    is LoadState.Error -> {
                        hideLoader()
                    }
                    else -> {
                        //
                    }
                }
            }
        }
    }

    companion object {
        private var defaultSelectedTab = 0
    }

}