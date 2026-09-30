package com.app.bharatphoton.fragment.profile

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
import com.app.bharatphoton.adapter.RenewProductListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage.notificationCount
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.FragmentRenewWarrantyBinding
import com.app.bharatphoton.module.warrantyApiResponse.WarrantyListResponse.WarrantyListData
import com.app.bharatphoton.viewModule.WarrantyViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RenewWarrantyFragment : BaseFragment<FragmentRenewWarrantyBinding>() {
    private var isFirstLoad = true
    private var isSelected = false
    private var selectedIndex = 0
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val renewProductListAdapter by lazy {
        RenewProductListAdapter(
            { data, type ->
                when (type) {
                    "root" -> {
                        warrantyListData = data
                        updateSubmitButtonVisibility()
                    }

                    "renewWarranty" -> {
                        val sinList = arrayListOf<String>()
                        data.finNo?.let { sinList.add(it) }

                        val bundle = Bundle().apply {
                            putString("ProductId", data.productId)
                            putString("orderId", data.orderId)
                            putStringArrayList("sinList", sinList)
                        }
                        navigateToScreen(
                            R.id.warrantyPlansFragment, bundle = bundle, navOptions = navOptions
                        )
                    }
                }
            },
            onSelectionChanged = {
                updateSubmitButtonVisibility()
            },
        )
    }

    private val viewModule by viewModels<WarrantyViewModule>()

    override fun onResume() {
        super.onResume()
        updateSubmitButtonVisibility()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiWarrantyListingRequest(type = defaultSelectedTab)
        optionLayout(defaultSelectedTab)
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentRenewWarrantyBinding = FragmentRenewWarrantyBinding.inflate(layoutInflater)

    private fun apiWarrantyListingRequest(type: Int) {
        viewModule.warrantyList(type = type).observe(viewLifecycleOwner) { list ->
            lifecycleScope.launch { renewProductListAdapter.submitData(list) }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            refresh { onComplete ->
                apiWarrantyListingRequest(type = selectedIndex)
                onComplete()
            }
            notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            handleOnPressBackButton { navigateBack() }
            topBar.title.text = getString(R.string.renew_warranty)
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                call.setOnAnimatedClickListener { openDialer() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            }
            submitBtn.setOnAnimatedClickListener {
                val selectedSinList = renewProductListAdapter.getSelectedSinList()
                if (selectedSinList.isEmpty()) {
                    showDialog(requireContext(), getString(R.string.please_select_at_least_one_subscription))
                    return@setOnAnimatedClickListener
                }

                warrantyListData?.let { data ->
                    val bundle = Bundle().apply {
                        putString("ProductId", data.productId)
                        putString("orderId", data.orderId)
                        putStringArrayList("sinList", ArrayList(selectedSinList))
                    }
                    navigateToScreen(R.id.warrantyPlansFragment, bundle, navOptions)
                }
            }
        }
    }

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
                    renewProductListAdapter.clearSelection()
                    updateSubmitButtonVisibility()
                    optionLayout(index)
                    selectedIndex = index
                    apiWarrantyListingRequest(type = index)

                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            productListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = renewProductListAdapter.withLoadStateFooter(LoaderAdapter())

                renewProductListAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            hideLoader()
                        }

                        LoadState.Loading -> showLoader()
                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (isFirstLoad) {
                                applyRecyclerAnimation(requireContext(), productListRv)
                                isFirstLoad = false
                            }
                            if (renewProductListAdapter.itemCount == 0) {
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

    private fun updateSubmitButtonVisibility() {
        val selected = renewProductListAdapter.getSelectedSinList()
        "selected -> $selected".mlog()
        binding.submitBtn.visibility = if (selected.isEmpty()) View.GONE else View.VISIBLE
    }


    companion object {
        private var warrantyListData: WarrantyListData? = null
        private var defaultSelectedTab = 0
    }
}