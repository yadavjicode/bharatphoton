package com.app.bharatphoton.fragment.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ProductListSubCategoryAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentViewAllSubcategoryProductListBinding
import com.app.bharatphoton.viewModule.ProductViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewAllSubcategoryProductListFragment : BaseFragment<FragmentViewAllSubcategoryProductListBinding>() {
    private val viewModule by viewModels<ProductViewModule>()
    private var subCategoryId = ""
    private var categoryId = ""
    private var isFirstTime = true
    private var productQuantity = 0
    private var progress: ProgressBar? = null
    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val adapters by lazy {
        ProductListSubCategoryAdapter { type, data, imageView, progressBar ->
            when (type) {
                "products" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data?.id?:"")
                    }
                    navigateToScreen(
                        id = R.id.productDetailsFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                "cart" -> {
                    productQuantity = data?.quantity ?: 0
                    progressBar?.visibility = View.VISIBLE
                    progress = progressBar
                    data?.id?.let {
                        apiAddToCartRequest(deviceId, it, 1)
                    }
                    com.app.bharatphoton.common.CircleAnimationUtil().attachActivity(activity).setTargetView(imageView).setImage(data?.image?.get(0)?.url).setMoveDuration(1000)
                        .setDestView(binding.topBar.cartImg).startAnimation()
                }

                "like" -> {
                    data?.id?.let { apiAddToWishListRequest(it) }
                }
            }
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentViewAllSubcategoryProductListBinding =
        FragmentViewAllSubcategoryProductListBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiProductListRequest(categoryId, deviceId)
        apiAddToWishListResponse()
        apiAddToCartResponse { data ->
            progress?.visibility = View.GONE
            SharedData.cartItemCount += 1
            binding.topBar.cartItemCount.text = (SharedData.cartItemCount).toString()
            apiProductListRequest(categoryId, deviceId)

        }
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()

        }
    }

    private fun apiProductListRequest(categoryId: String, deviceId: String) {
        lifecycleScope.launch {
            viewModule.subCategoryProductList(categoryId = categoryId, subcategoryId = subCategoryId, deviceId = deviceId).collectLatest {
                adapters.submitData(it)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            productRv.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = adapters

                adapters.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> hideLoader()
                        LoadState.Loading -> showLoader()
                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (isFirstTime) {
                                applyRecyclerAnimation(context = requireContext(), recyclerView = binding.productRv)
                                isFirstTime = false
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

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.products)
            handleOnPressBackButton { navigateBack() }
            subCategoryId = arguments?.getString("subcategoryId", "") ?: ""
            categoryId = arguments?.getString("categoryId", "") ?: ""
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, null, navOptions) }

           swipeRefreshLayout.setOnRefreshListener {
             swipeRefreshLayout.isRefreshing = false
            }

        }
    }
}