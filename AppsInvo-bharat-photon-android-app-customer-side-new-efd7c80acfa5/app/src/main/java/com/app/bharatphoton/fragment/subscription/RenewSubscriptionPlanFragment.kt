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
import com.app.bharatphoton.adapter.RenewSubscriptionListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage.notificationCount
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.databinding.FragmentRenewSubscriptionPlanBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPurchaseSubscriptionListResponse.RenewPurchaseSubscriptionData
import com.app.bharatphoton.viewModule.PurchaseSubscriptionViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RenewSubscriptionPlanFragment : BaseFragment<FragmentRenewSubscriptionPlanBinding>() {


    private var isLoaded = true
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val renewSubscriptionListAdapter by lazy {
        RenewSubscriptionListAdapter(onClick = { data, type ->
            when (type) {
                "root" -> {
                    renewPurchaseSubscriptionData = data
                    updateSubmitButtonVisibility()
                }

                "renewWarranty" -> {
                    val sinList = mutableListOf<String>()
                    data.sin?.let { sinList.add(it) }
                    val sinParam = sinList.joinToString(",") { it.trim() }

                    val bundle = Bundle().apply {
                        putString("subscriptionId", data.orderId)
                        putString("sinList", sinParam)
                    }
                    navigateToScreen(R.id.renewPlanFragment, bundle, navOptions)
                }
            }
        }, onSelectionChanged = {
            updateSubmitButtonVisibility()
        })
    }

    private val viewModule by viewModels<PurchaseSubscriptionViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiPurchasedSubscriptionListingRequest(id = "", type = 1, installationId = "")
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRenewSubscriptionPlanBinding = FragmentRenewSubscriptionPlanBinding.inflate(layoutInflater)

    private fun apiPurchasedSubscriptionListingRequest(
        id: String, type: Int, installationId: String
    ) {
        lifecycleScope.launch {
            viewModule.renewPurchasedSubscriptionListing(id = id, type = type, installationId = installationId).collectLatest { list ->
                renewSubscriptionListAdapter.submitData(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateSubmitButtonVisibility()
    }

    private fun initData() {
        with(binding) {
            handleOnPressBackButton { navigateBack() }
            topBar.apply {
                title.text = getString(R.string.renew_subscription)

                cartItemCount.text = SharedData.cartItemCount.toString()
                notificationCountTv.text = SharedData.notificationCount.toString()
                topBar.topBarFunctionality.visibility = View.VISIBLE
            }
            refresh { onComplete ->
                apiPurchasedSubscriptionListingRequest(id = "", type = 1, installationId = "")
                onComplete()

            }
            notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
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
                navigateToScreen(id = R.id.notificationFragment, null, navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(id = R.id.cartFragment, null, navOptions)
            }

            submitBtn.setOnAnimatedClickListener {
                val selectedSinList = renewSubscriptionListAdapter.getSelectedSinList()
                if (selectedSinList.isEmpty()) {
                    showDialog(requireContext(), getString(R.string.please_select_at_least_one_subscription))
                    return@setOnAnimatedClickListener
                }

                renewPurchaseSubscriptionData?.let { data ->
                    val bundle = Bundle().apply {
                        putString("subscriptionId", data.orderId)
                        putStringArrayList("sinList", ArrayList(selectedSinList))
                    }
                    navigateToScreen(R.id.renewPlanFragment, bundle, navOptions)
                }
            }

        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            subscriptionListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = renewSubscriptionListAdapter.withLoadStateFooter(LoaderAdapter())

                renewSubscriptionListAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> hideLoader()
                        LoadState.Loading -> showLoader()
                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (isLoaded) {
                                applyRecyclerAnimation(requireContext(), subscriptionListRv)
                                isLoaded = false
                            }
                            if (renewSubscriptionListAdapter.itemCount == 0) {
                                notFound.visibility = View.VISIBLE
                            } else {
                                subscriptionListRv.visibility = View.VISIBLE
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

    private fun updateSubmitButtonVisibility() {
        if (renewSubscriptionListAdapter.getSelectedSinList().isEmpty()) {
            binding.submitBtn.visibility = View.GONE
        } else {
            binding.submitBtn.visibility = View.VISIBLE
        }
    }

    companion object {
        private var renewPurchaseSubscriptionData: RenewPurchaseSubscriptionData? = null
    }

}