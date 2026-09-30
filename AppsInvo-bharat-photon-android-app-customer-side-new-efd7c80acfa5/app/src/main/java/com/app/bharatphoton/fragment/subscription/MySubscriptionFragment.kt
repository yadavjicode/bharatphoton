package com.app.bharatphoton.fragment.subscription

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
import com.app.bharatphoton.adapter.MySubscriptionAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentMySubscriptionBinding
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.viewModule.PurchaseSubscriptionViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MySubscriptionFragment : BaseFragment<FragmentMySubscriptionBinding>() {


    private var isLoaded = true

    private val mySubscriptionAdapter by lazy {
        MySubscriptionAdapter(onClick = { type, data, parentData ->
            when (type) {
                "root" -> {
                    val bundle = Bundle().apply {
                        putString("subscriptionId", parentData.id)
                        putString("installationId", data.id)
                    }
                    navigateToScreen(R.id.mySubscriptionDetailsFragment, bundle, navOptions)
                }

                "downloadReport" -> {
                    val bundle = Bundle().apply {
                        putString("orderId", parentData.id ?: "")
                        putString("productId", parentData.productId ?: "")
                        putString("screenType", "Subscription")
                    }
                    navigateToScreen(R.id.monthlyGraphFragment, bundle, navOptions)
                }

                "reNewPlan" -> {
                    val sinList = mutableListOf<String>()
                    data.sin?.let { sinList.add(it) }
                    val sinParam = sinList.joinToString(",") { it.trim() }

                    val bundle = Bundle().apply {
                        putString("subscriptionId", parentData.id)
                        putString("sinList", sinParam)
                    }
                    navigateToScreen(R.id.renewPlanFragment, bundle, navOptions)
                }

                else -> {
                    //
                }

            }

        }, clickUploadPhoto = { type, data, selectedMetric ->
            when (type) {
                "uploadPhoto" -> {
                    val bundle = Bundle().apply {
                        putString("orderId", data.id)
                        putString("installationId", data.installationAddressId)
                        putString("orderNo", data.uuid)
                    }
                    navigateToScreen(R.id.uploadPhotoFragment, bundle, navOptions)
                }

                "registerComplaint" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data.productId)
                        putString("productName", data.productName)
                        putString("categoryName", data.categoryName)
                        putString("categoryId", data.categoryId)
                        putString("subscriptionId", data.id)
                        putString("sin", selectedMetric.sin)
                        putInt("complaintType", 2)
                    }
                    navigateToScreen(
                        id = R.id.complaintRegisterFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                else -> {
                    //
                }
            }
        })
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<PurchaseSubscriptionViewModule>()
    private val mySubscriptionList = ArrayList<Module>()

    override fun onDestroyView() {
        super.onDestroyView()
        mySubscriptionList.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiPurchasedSubscriptionListingRequest("", type = 0, installationId = "")
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMySubscriptionBinding = FragmentMySubscriptionBinding.inflate(layoutInflater)

    private fun apiPurchasedSubscriptionListingRequest(id: String, type: Int, installationId: String) {
        lifecycleScope.launch {
            viewModule.purchasedSubscriptionListing(id = id, type = type, installationId = installationId).collectLatest { list ->
                mySubscriptionAdapter.submitData(list)
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
            topBar.title.text = getString(R.string.my_subscription)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            handleOnPressBackButton { navigateBack() }

            refresh { onComplete ->
                apiPurchasedSubscriptionListingRequest("", 0, installationId = "")
                onComplete()
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
            renewPlanBtn.setOnAnimatedClickListener {
                navigateToScreen(R.id.renewPlanFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            mySubscriptionRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                mySubscriptionRv.adapter = mySubscriptionAdapter.withLoadStateFooter(LoaderAdapter())

                mySubscriptionAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> hideLoader()
                        LoadState.Loading -> showLoader()
                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (isLoaded) {
                                applyRecyclerAnimation(requireContext(), mySubscriptionRv)
                                isLoaded = false
                            }
                            notFound.visibility = if (mySubscriptionAdapter.itemCount == 0) View.VISIBLE else View.GONE
                        }

                        else -> {
                            //
                        }
                    }
                }
            }
        }
    }

}