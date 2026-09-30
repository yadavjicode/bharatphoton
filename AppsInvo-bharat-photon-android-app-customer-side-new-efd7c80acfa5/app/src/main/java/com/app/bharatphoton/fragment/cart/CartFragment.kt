package com.app.bharatphoton.fragment.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.CartItemListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.SharedData.navigateBackScreen
import com.app.bharatphoton.common.TAG
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.FragmentCartBinding
import com.app.bharatphoton.module.cartResponse.CartDetailsResponse
import com.app.bharatphoton.module.cartResponse.CartDetailsResponse.Data.Cart.Product
import com.app.bharatphoton.module.cartResponse.DeleteCartItemResponse
import com.app.bharatphoton.retrofit.Constants
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {
    private var cartId = ""
    private var countTextView: TextView? = null
    private var countImage: View? = null
    private var isLoaded = true
    private var progress: ProgressBar? = null

    private val cartItemListAdapter: CartItemListAdapter by lazy {
        CartItemListAdapter { data, type, _, imageView, progressBar, countTv ->
            when (type) {
                "delete" -> handleDeleteAction(data)
                "plus" -> handleCartUpdate(data, 1, imageView, progressBar, countTv)
                "minus" -> handleCartUpdate(data, -1, imageView, progressBar, countTv)
                else -> {
                    //
                }
            }
        }
    }

    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val viewModule by viewModels<CartViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiCartDetailsRequest()
        apiCartDetailsResponse()
        apiAddToCartResponse { onSuccess ->
            progress?.visibility = View.GONE
            countTextView?.visibility = View.VISIBLE
            apiCartDetailsRequest()
            countImage?.isEnabled = true
            onSuccess.data?.products?.let { products ->
                if (products.isNotEmpty()) {
                    products.forEach { product ->
                        product?.quantity?.let { quantity ->
                            countTextView?.text = quantity.toString()
                        }
                    }
                }
            }
        }
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()
        }
        apiRemoveFromCartResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCartBinding = FragmentCartBinding.inflate(layoutInflater)

    private fun apiCartDetailsRequest() {
        viewModule.cartDetails(deviceId = deviceId)
    }


    private fun handleDeleteAction(data: Product) {
        apiRemoveFromCartRequest(cartId = cartId, id = data.id.toString())
        SharedData.cartItemCount -= 1
        binding.topBar.cartItemCount.text = SharedData.cartItemCount.toString()

        with(binding) {
            if (SharedData.cartItemCount == 0) {
                cartLayout.visibility = View.GONE
                emptyCartLayout.visibility = View.VISIBLE
                bottomLayout.visibility = View.GONE
            } else {
                emptyCartLayout.visibility = View.GONE
                cartLayout.visibility = View.VISIBLE
                bottomLayout.visibility = View.VISIBLE
            }
        }
        cartItemListAdapter.removeItem(data)
    }

    private fun handleCartUpdate(data: Product, quantity: Int, imageView: View?, progressBar: ProgressBar?, countTv: TextView?) {
        apiAddToCartRequest(deviceId = deviceId, productId = data.productId ?: "", quantity = quantity)

        val sourceView = if (quantity > 0) imageView else binding.topBar.cartImg
        val destView = if (quantity > 0) binding.topBar.cartImg else imageView

        com.app.bharatphoton.common.CircleAnimationUtil().attachActivity(activity).setTargetView(sourceView).setImage(data.image?.get(0)?.url).setMoveDuration(1000).setDestView(destView).startAnimation()

        progress = progressBar
        countTextView = countTv
        countImage = imageView

        progressBar?.visibility = View.VISIBLE
        countTv?.visibility = View.GONE
        imageView?.visibility = if (countTv?.text.toString() > "0") View.VISIBLE else View.INVISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun apiCartDetailsResponse() {
        viewModule.cartDetails.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Loading) {
                showLoader()
            }
            if (it is NetworkResult.Error) {
                hideLoader()
            }
            if (it is NetworkResult.Success) {
                with(binding) {
                    cartFragment.visibility = View.VISIBLE
                    hideLoader()
                    try {
                        cartDetailsResponse = Gson().fromJson(it.data, CartDetailsResponse::class.java)
                        if (cartDetailsResponse?.code == 1) {
                            val data = cartDetailsResponse?.data?.cart?.products
                            if (data?.isNotEmpty() == true) {
                                cartItemListAdapter.submitList(data)
                            }
                            if (isLoaded) {
                                RecyclerAnimationHelper.animation(requireContext(), root)
                                isLoaded = false
                            }

                            if (cartDetailsResponse?.data?.cart?.id == null) {
                                cartLayout.visibility = View.GONE
                                emptyCartLayout.visibility = View.VISIBLE
                                bottomLayout.visibility = View.GONE
                            } else {
                                emptyCartLayout.visibility = View.GONE
                                cartLayout.visibility = View.VISIBLE
                                bottomLayout.visibility = View.VISIBLE
                            }

                            if (cartDetailsResponse?.data?.cart?.discountAmount == 0.0) {
                                discountPrice.visibility = View.GONE
                                discountTxt.visibility = View.GONE
                            } else {
                                discountPrice.visibility = View.VISIBLE
                                discountTxt.visibility = View.VISIBLE
                            }

                            cartId = cartDetailsResponse?.data?.cart?.id.toString()
                            val actualPrice = formatPrice(price = cartDetailsResponse?.data?.cart?.totalAmount ?: 0.0, currencyType = "INR")
                            val taxPrice = formatPrice(price = cartDetailsResponse?.data?.cart?.taxAmount ?: 0.0, currencyType = "INR")
                            formatPrice(price = cartDetailsResponse?.data?.cart?.finalTotalAmount ?: 0.0, currencyType = "INR")
                            val discountedAmount = formatPrice(price = cartDetailsResponse?.data?.cart?.finalTotalAmount ?: 0.0, currencyType = "INR")
                            val discountAmount = formatPrice(price = cartDetailsResponse?.data?.cart?.discountAmount ?: 0.0, currencyType = "INR")

                            totalPrice.text = actualPrice
                            tax.text = taxPrice
                            subTotal.text = discountedAmount
                            discountPrice.text = discountAmount
                            apiCartCountRequest(deviceId = deviceId)

                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }
    }

    private fun apiRemoveFromCartRequest(cartId: String, id: String) {
        val request = JsonObject().apply {
            addProperty("cartId", cartId)
            addProperty("id", id)
        }
        viewModule.removeFromCart(request)
    }

    private fun apiRemoveFromCartResponse() {
        viewModule.removeFromCart.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> hideLoader()
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        deleteCartItemResponse = Gson().fromJson(it.data, DeleteCartItemResponse::class.java)
                        if (deleteCartItemResponse?.code == 1) {
                            apiCartDetailsRequest()
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

    private fun initData() {
        with(binding) {
            apiCartCountRequest(deviceId = deviceId)
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.cart)

            handleOnPressBackButton {
                navigateBackScreen = ""
                navigateBack()
            }
            refresh { onComplete ->
                apiCartDetailsRequest()
                onComplete()
            }
            if (sharedPrefManager.getString(Constants.SharedPref.AUTH_TOKEN).isBlank()) {
                couponsLayout.alpha = 0.5f
                couponsLayout.isClickable = false
                couponsLayout.isEnabled = false

            } else {
                couponsLayout.alpha = 1f
                couponsLayout.isClickable = true
                couponsLayout.isEnabled = true
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBackScreen = ""
                navigateBack()
            }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }

            couponsLayout.setOnAnimatedClickListener {
                val fragment = OffersAndCouponsFragment {
                    apiCartDetailsRequest()
                }
                val bundle = Bundle().apply {
                    putString("cartId", cartId)
                }
                fragment.arguments = bundle
                fragment.show(childFragmentManager, TAG)

            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            checkoutBtn.setOnAnimatedClickListener {
                if (sharedPrefManager.getString(Constants.SharedPref.AUTH_TOKEN).isBlank()) {
                    navigateToScreen(R.id.loginFragment, bundle = null, navOptions = navOptions)
                } else {
                    val allProductsValid = cartDetailsResponse?.data?.cart?.products?.all { product ->
                        product?.productquantity!! > 0 || product.isQuantityUnlimited == 1
                    } == true
                    if (allProductsValid) {
                        val bundle = Bundle().apply {
                            putString("cartId", cartId)
                        }
                        navigateToScreen(
                            R.id.checkOutFragment, bundle, navOptions = navOptions
                        )
                    }
                }
            }
            exploreMore.setOnAnimatedClickListener {
                navigateToScreen(R.id.homeFragment, null, navOptions = navOptions)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            itemCartListRv.apply {
                itemAnimator = null
                layoutManager = LinearLayoutManager(requireContext())
                adapter = cartItemListAdapter
            }
        }
    }

    companion object {
        private var cartDetailsResponse: CartDetailsResponse? = null
        private var deleteCartItemResponse: DeleteCartItemResponse? = null
    }
}