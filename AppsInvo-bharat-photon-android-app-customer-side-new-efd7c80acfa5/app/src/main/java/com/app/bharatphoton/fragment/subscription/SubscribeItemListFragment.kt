package com.app.bharatphoton.fragment.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.updateRatingStars
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSubscribeItemListBinding
import com.app.bharatphoton.module.cartSubscriptionResponse.SubscriptionCartLIstResponse
import com.app.bharatphoton.module.cartSubscriptionResponse.UpdateQuantityResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartSubscriptionViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SubscribeItemListFragment : BaseFragment<FragmentSubscribeItemListBinding>() {
    private var count = 0
    private var id = ""
    private var isLoaded = true
    private var amount = ""
    private var subscriptionCartId = ""
    private var previousOrderId = ""
    private var screenType = "subscribeList"
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val viewModule by viewModels<CartSubscriptionViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiSubscriptionCartListRequest(id)
        apiSubscriptionListResponse()
        apiUpdateQuantityResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSubscribeItemListBinding = FragmentSubscribeItemListBinding.inflate(layoutInflater)

    private fun apiSubscriptionCartListRequest(id: String) {
        viewModule.subscriptionCartList(id)
    }

    @SuppressLint("SetTextI18n")
    private fun apiSubscriptionListResponse() {
        viewModule.subscriptionCartList.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        binding.subscribeItemListFragment.visibility = View.VISIBLE
                        subscriptionCartLIstResponse = Gson().fromJson(it.data, SubscriptionCartLIstResponse::class.java)
                        if (subscriptionCartLIstResponse?.code == 1) {
                            with(binding) {
                                val data = subscriptionCartLIstResponse?.data
                                if (isLoaded) {
                                    RecyclerAnimationHelper.animation(requireContext(), root)
                                    isLoaded = false
                                }
                                if (data?.image?.isNotEmpty() == true) {
                                    Glide.with(requireContext()).load(data.image[0]?.url).placeholder(R.drawable.placeholder).into(productImg)
                                }
                                val planValue = formatPrice(price = data?.price?.toDouble() ?: 0.0, currencyType = "INR")
//                                val taxValue = formatPrice(price = data?.taxAmount?.toDouble() ?: 0.0, currencyType = "INR")
                                val subTotalAmount = formatPrice(price = data?.finalTotalAmount ?: 0.0, currencyType = "INR")
                                productName.text = data?.productName ?: ""
                                qtyTxt.text = "QTY (${data?.quantity.toString()})"
                                qty.text = "${data?.quantity.toString()}x"
                                planPrice.text = "$planValue /Incl. GST"

                                subTotal.text = subTotalAmount
                                amount = data?.finalTotalAmount.toString()
                                subscriptionCartId = data?.id ?: ""

                                when (data?.planType) {
                                    0 -> planType.text = "Plan (Lite)"
                                    1 -> planType.text = "Plan (Plus)"
                                    2 -> planType.text = "Plan (Premium)"
                                }

                                data?.description?.let {
                                    val cleanedDescription = it.replace("<br>", "").replace("<p>", "")
                                    productDescription.text = Html.fromHtml(cleanedDescription, Html.FROM_HTML_MODE_COMPACT)

                                    Utils.setupExpandableTextView(
                                        productDescription, viewMore = viewMore, data.description, initialMaxLines = 1
                                    )
                                }
                                updateRatingStars(rating = data?.rating ?: 0, view = ratingLayout, context = requireContext())
                                if (data?.quantity == 0) {
                                    countTv.text = "1"
                                } else {
                                    countTv.text = data?.quantity.toString()
                                }
                            }

                        } else {
                            requireActivity().toast(subscriptionCartLIstResponse?.message ?: "")
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

    private fun apiUpdateQuantityRequest(id: String, quantity: Int) {
        val request = JsonObject().apply {
            addProperty("id", id)
            addProperty("quantity", quantity)
        }
        viewModule.updateQuantity(request = request)
    }

    @SuppressLint("SetTextI18n")
    private fun apiUpdateQuantityResponse() {
        viewModule.updateQuantity.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.progressBar.visibility = View.VISIBLE
                is NetworkResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    try {
                        updateQuantityResponse = Gson().fromJson(it.data, UpdateQuantityResponse::class.java)
                        if (updateQuantityResponse?.code == 1) {
                            count = updateQuantityResponse?.data?.quantity ?: 0
                            val subTotalAmount = updateQuantityResponse?.data?.totalAmount ?: 0.0
                            val formatedSubTotalAmount = formatPrice(price = subTotalAmount.toDouble(), currencyType = "INR")
                            binding.apply {
                                countTv.text = updateQuantityResponse?.data?.quantity.toString()
                                qtyTxt.text = "QTY (${updateQuantityResponse?.data?.quantity.toString()})"
                                qty.text = "${updateQuantityResponse?.data?.quantity.toString()}x"
                                subTotal.text = formatedSubTotalAmount
                            }

                        } else {
                            requireActivity().toast(updateQuantityResponse?.message ?: "")
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
            topBar.cart.visibility = View.GONE
            topBar.title.text = getString(R.string.subscribe)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions)
            }
            handleOnPressBackButton { navigateBack() }
            id = arguments?.getString("id", "") ?: ""
            screenType = arguments?.getString("type", "subscribeList") ?: ""
            previousOrderId = arguments?.getString("previousOrderId", "") ?: ""
            plus.visibility = if (screenType == "renewPlan") View.GONE else View.VISIBLE
            minus.visibility = if (screenType == "renewPlan") View.GONE else View.VISIBLE
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }

            plus.setOnAnimatedClickListener {
                apiUpdateQuantityRequest(id = id, quantity = 1)
            }
            minus.setOnAnimatedClickListener {
                if (count > 1) apiUpdateQuantityRequest(id = id, quantity = -1)
            }


            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            continueBtn.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("type", screenType)
                    putString("subscriptionCartId", subscriptionCartId)
                    putString("amount", amount)
                    putString("previousOrderId", previousOrderId)
                }
                navigateToScreen(R.id.paymentFragment, bundle, navOptions = navOptions)
            }
        }
    }

    companion object {
        private var subscriptionCartLIstResponse: SubscriptionCartLIstResponse? = null
        private var updateQuantityResponse: UpdateQuantityResponse? = null
    }
}