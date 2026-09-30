package com.app.bharatphoton.fragment.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.HomeProductsAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentCategoriesBinding
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse
import com.app.bharatphoton.retrofit.NetworkResult.Loading
import com.app.bharatphoton.retrofit.NetworkResult.Success
import com.app.bharatphoton.viewModule.ListingViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class CategoriesFragment : BaseFragment<FragmentCategoriesBinding>() {

    private val categoryAdapter by lazy {
        HomeProductsAdapter { type, data ->
            when (type) {
                "products" -> {
                    val bundle = Bundle().apply {
                        putString("categoryId", data.id?:"")
                    }
                    navigateToScreen(id = R.id.productsFragment, bundle = bundle, navOptions = navOptions)
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

    private val viewModule by viewModels<ListingViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleOnPressBackButton {
            navigateBack()
        }
        onClick()
        setDataInAdapter()
        apiCategoryLisRequest()
        apiCategoryLisResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCategoriesBinding = FragmentCategoriesBinding.inflate(layoutInflater)

    private fun initData() {
        with(binding) {
            val deviceId = sharedPrefManager.getString("deviceId")
            apiCartCountRequest(deviceId = deviceId)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { _ ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            RecyclerAnimationHelper.animation(requireContext(),root)
            apiCartCountResponse { count ->
                topBar.cartItemCount.text = count.toString()
            }
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()

            refresh { onComplete ->
                apiCategoryLisRequest()
                apiCategoryLisResponse()
                onComplete()
            }
        }
    }

    private fun apiCategoryLisRequest() {
        viewModule.categoryListing()
    }

    override fun onResume() {
        super.onResume()
        setDataIntoViews()
    }

    private fun apiCategoryLisResponse() {
        viewModule.categoryList.observe(viewLifecycleOwner) {
            if (it is Loading && categoryListingResponse == null) showLoader() else hideLoader()
            if (it is Error) {
                try {
                    val message = JSONObject(it.errorMessage ?: "").optString("message")
                    requireActivity().toast(message)
                } catch (e: Exception) {
                    //
                }
            }
            if (it is Success) {
                try {
                    categoryListingResponse = Gson().fromJson(it.data, CategoryListingResponse::class.java)
                    if (categoryListingResponse?.code == 1) {
                        RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.categoryRv)
                        setDataIntoViews()
                    } else {
                        requireActivity().toast(categoryListingResponse?.message ?: "")
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    private fun setDataIntoViews() {
        categoryListingResponse?.data?.let {
            categoryAdapter.submitList(it)
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            categoryRv.apply {
                itemAnimator = null
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = categoryAdapter
            }
        }
    }

    companion object {
        private var categoryListingResponse: CategoryListingResponse? = null
    }
}