package com.app.bharatphoton.fragment.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.SubscriptionListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSubscribeBinding
import com.app.bharatphoton.module.subcriptionResponse.SubscriptionListingResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartSubscriptionViewModule
import com.app.bharatphoton.viewModule.SubscriptionViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@Suppress("SameParameterValue")
@AndroidEntryPoint
class SubscribeFragment : BaseFragment<FragmentSubscribeBinding>() {


    private var isLoaded = true
    private var pinCode = ""
    private var productId = ""
    private val cartSubscriptionViewModule by viewModels<CartSubscriptionViewModule>()
    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }

    private val subscriptionListAdapter by lazy {
        SubscriptionListAdapter { data, type ->
            when (type) {
                "purchase" -> {
                    apiAddSubscriptionToCartRequest(1, subscriptionId = data.id ?: "", productId = data.productId ?: "", finNumbers = listOf())
                }
            }
        }
    }

    private val viewModule by viewModels<SubscriptionViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.subscriptionListing.postValue(null)
        cartSubscriptionViewModule.addSubscriptionToCart.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiSubscriptionListingRequest()
        apiSubscriptionListingResponse()

        apiAddSubscriptionToCartResponse { data ->
            val bundle = Bundle().apply {
                putString("id", data.data?.id)
            }
            navigateToScreen(R.id.subscribeItemListFragment, bundle, navOptions = navOptions)
        }

        setDataInAdapter()


    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSubscribeBinding = FragmentSubscribeBinding.inflate(layoutInflater)

    private fun apiSubscriptionListingRequest() {
        viewModule.subscriptionListing(productId = productId, pincode = pinCode)
    }

    private fun apiSubscriptionListingResponse() {
        viewModule.subscriptionListing.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        binding.subscribeFragment.visibility = View.VISIBLE
                        subscriptionListingResponse = Gson().fromJson(it.data, SubscriptionListingResponse::class.java)
                        if (subscriptionListingResponse?.code == 1) {
                            val data = subscriptionListingResponse?.data
                            val cleanList = subscriptionListingResponse?.data?.filterNotNull()?.distinctBy { it.id }
                            subscriptionListAdapter.submitList(cleanList)


                            if (isLoaded) {
                                animation(requireContext(), binding.root)
                                isLoaded = false
                            }

                            if (data?.isNotEmpty() == true) {
                                binding.productName.text = data[0]?.productName ?: ""
                                binding.categoryName.text = data[0]?.categoryName ?: ""
                                binding.modelName.text = data[0]?.modelNo ?: ""

                                if (data[0]?.productImage?.get(0)?.url?.isNotBlank() == true) {
                                    Glide.with(requireContext()).load(data[0]?.productImage?.get(0)?.url ?: "").placeholder(R.drawable.placeholder).into(binding.productImg)
                                }
                            }
                        } else {
                            requireActivity().toast(subscriptionListingResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            topBar.apply {
                title.text = getString(R.string.subscribe)
                cart.visibility = View.GONE
                notificationCountTv.text = SharedData.notificationCount.toString()
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
            }

            pinCode = arguments?.getString("pinCode", "") ?: ""
            productId = arguments?.getString("productId", "") ?: ""
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            subscriptionListRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = subscriptionListAdapter
            }
        }
    }

    companion object {
        private var subscriptionListingResponse: SubscriptionListingResponse? = null

    }

}