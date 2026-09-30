package com.app.bharatphoton.fragment.product

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
import com.app.bharatphoton.adapter.MyProductListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.navigateBackScreen
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentMyProductBinding
import com.app.bharatphoton.viewModule.ProductViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProductFragment : BaseFragment<FragmentMyProductBinding>() {

    private var count = 0
    private var isFirstLoad = true
    private var orderId = ""
    private var productId = ""

    private val myProductListAdapter by lazy {
        MyProductListAdapter { data, type, orderId ->
            when (type) {
                "ModuleLayout" -> {
                    productId = data.productId ?: ""
                    this.orderId = orderId?:""
                    val bottomSheetFragment = ProductInstalledDetailsBottomSheetFragment().apply {
                        arguments = Bundle().apply {
                            putString("id", data.productId?:"")
                            putString("orderId", orderId)
                        }
                    }
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                }
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<ProductViewModule>()

    override fun onResume() {
        super.onResume()
        if (count != 0 && navigateBackScreen == "SinDetailsScreen") {
            val bottomSheetFragment = ProductInstalledDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("id", productId)
                    putString("orderId", orderId)
                }
            }
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
        count += 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiInstalledProductListRequest()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMyProductBinding = FragmentMyProductBinding.inflate(layoutInflater)

    private fun apiInstalledProductListRequest() {
        lifecycleScope.launch {
            viewModule.installedProductList(id = "", orderId = "").collectLatest { list ->
                myProductListAdapter.submitData(list)
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
            topBar.title.text = getString(R.string.my_products)
            topBar.topBarFunctionality.visibility = View.VISIBLE

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

    private fun setDataInAdapter() {
        with(binding) {
            myProductList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myProductListAdapter.withLoadStateFooter(LoaderAdapter())

                myProductListAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            hideLoader()
                        }

                        LoadState.Loading -> {
                            showLoader()
                        }

                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (isFirstLoad) {
                                RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), myProductList)
                                isFirstLoad = false
                            }

                            if (myProductListAdapter.itemCount < 1) {
                                noDataFound.visibility = View.VISIBLE
                            } else {
                                noDataFound.visibility = View.GONE

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
}