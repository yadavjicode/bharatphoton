package com.app.bharatphoton.fragment.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.CheckoutOrderItemListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.type
import com.app.bharatphoton.common.TAG
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.hideKeyboard
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentCheckOutBinding
import com.app.bharatphoton.module.checkoutResponse.CheckInstallationResponse
import com.app.bharatphoton.module.checkoutResponse.CheckOutResponse
import com.app.bharatphoton.module.checkoutResponse.RemoveInstallationResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CheckOutViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class CheckOutFragment : BaseFragment<FragmentCheckOutBinding>() {
    private var addressId = ""
    private var cartId = ""
    private var isLoaded = true
    private var finalAmount = ""
    private var subtotalValue = ""

    private val checkoutOrderItemListAdapter: CheckoutOrderItemListAdapter by lazy {
        CheckoutOrderItemListAdapter { data, type ->
            when (type) {
                "delete" -> {
                    apiRemoveInstallationRequest(id = data.id ?: "")
                }

                else -> {

                }
            }
        }
    }

    /*

    */

    private val checkoutViewModule by viewModels<CheckOutViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiCheckoutRequest(cartId)
        apiCheckOutResponse()
        apiCheckInstallationResponse()
        hideKeyBoardWithPinCode()
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()
        }
        apiRemoveInstallation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        checkoutViewModule.checkOut.postValue(null)
        checkoutViewModule.checkInstallation.postValue(null)
        checkoutViewModule.removeInstallation.postValue(null)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCheckOutBinding = FragmentCheckOutBinding.inflate(layoutInflater)

    private fun apiCheckInstallationRequest() {
        with(binding) {
            val request = JsonObject().apply {
                val pin = pinCode.text.toString()
                addProperty("pincode", pin.toInt())
                addProperty("cartId", cartId)
            }
            checkoutViewModule.checkInstallation(request)
        }
    }

    private fun apiCheckInstallationResponse() {
        checkoutViewModule.checkInstallation.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.checkBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.checkBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.checkBtn.hideLoading()
                    try {
                        checkInstallationResponse = Gson().fromJson(it.data, CheckInstallationResponse::class.java)
                        if (checkInstallationResponse?.code == 1) {
                            binding.installationTv.visibility = View.VISIBLE
                            apiCheckoutRequest(cartId)
                        } else {
                            binding.installationTv.visibility = View.GONE
                            showDialog(context = requireContext(), messageText = checkInstallationResponse?.message.toString())
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

    private fun apiRemoveInstallationRequest(id: String) {
        val request = JsonObject().apply {
            addProperty("cartId", cartId)
            addProperty("id", id)
        }
        checkoutViewModule.removeInstallation(request)
    }

    private fun apiRemoveInstallation() {
        checkoutViewModule.removeInstallation.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        removeInstallationResponse = Gson().fromJson(it.data, RemoveInstallationResponse::class.java)
                        if (removeInstallationResponse?.code == 1) {
                            apiCheckoutRequest(cartId)
                        } else {
                            requireActivity().toast(removeInstallationResponse?.message ?: "")
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
    private fun apiCheckOutResponse() {
        checkoutViewModule.checkOut.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val message = JSONObject(it.errorMessage ?: "").getString("message")
                        requireActivity().toast(message)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        checkOutResponse = Gson().fromJson(it.data, CheckOutResponse::class.java)
                        if (checkOutResponse?.code == 1) {
                            with(binding) {
                                if (isLoaded) {
                                    animation(requireContext(), root)
                                    isLoaded = false
                                }
                                checkOutFragment.visibility = View.VISIBLE
                                val data = checkOutResponse?.data
                                if (data?.address?.id == null) {
                                    change.text = "Add"
                                    addressLayout.visibility = View.GONE
                                    requireActivity().toast(getString(R.string.please_add_address))
                                } else {
                                    change.text = "Change"
                                    addressLayout.visibility = View.VISIBLE
                                    addressTv.text = "${data.address.address} \n${data.address.state} ${data.address.city}"
                                    stateTv.text = data.address.state
                                    phoneNumberTv.text = data.address.countryCode + data.address.phoneNo
                                }

                                /*        if (data?.cart?.products?.get(0)?.installationPrice != null) {
                                            type = "Available"

                                        }*/
                                // CheckBox Logic
                                data?.cart?.products?.forEach {
                                    it?.installationPrice?.let { it1 ->
                                        if (it != null) {
                                            checkbox.setImageResource(R.drawable.check)
                                            enterPinCodeLayout.visibility = View.VISIBLE
                                            installationTv.visibility = View.VISIBLE
                                            data.cart.isClicked = 1
                                        } else {
                                            checkbox.setImageResource(R.drawable.uncheck)
                                            enterPinCodeLayout.visibility = View.GONE
                                            installationTv.visibility = View.GONE
                                            data.cart.isClicked = 0
                                        }
                                    }
                                }

                                if (data?.coupon?.couponCodes?.isNotEmpty() == true) {
                                    couponsTv.text = data.coupon.couponCodes.joinToString(", ")
                                } else {
                                    couponsTv.text = getString(R.string.coupons_offers)
                                }

                                addressId = data?.address?.id ?: ""
                                val totalPriceValue = formatPrice(data?.priceSummary?.totalAmount ?: 0.0, currencyType = "INR")
                                val taxValue = formatPrice(data?.priceSummary?.taxAmount ?: 0.0, currencyType = "INR")
                                val couponValue = formatPrice(data?.priceSummary?.discountAmount?.toDouble() ?: 0.0, currencyType = "INR")
                                subtotalValue = formatPrice(data?.priceSummary?.finalTotalAmount ?: 0.0, currencyType = "INR")
                                val totalValue = formatPrice(data?.priceSummary?.finalTotalAmountAfterDiscount ?: 0.0, currencyType = "INR")
                                finalAmount = data?.priceSummary?.finalTotalAmount.toString()
                                totalPrice.text = totalPriceValue
                                tax.text = taxValue

                                if (data?.priceSummary?.discountAmount == 0) {
                                    coupon.visibility = View.GONE
                                    couponTxt.visibility = View.GONE
                                } else {
                                    coupon.visibility = View.VISIBLE
                                    couponTxt.visibility = View.VISIBLE
                                    coupon.text = couponValue
                                }
                                subTotal.text = subtotalValue
                                payNowBtn.text = getString(R.string.pay_now, subtotalValue)
                                cartId = data?.cart?.id ?: ""
                            }
                            checkoutOrderItemListAdapter.submitList(checkOutResponse?.data?.cart?.products)
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

    private fun apiCheckoutRequest(cartId: String) {
        checkoutViewModule.checkout(cartId = cartId)
    }

    private fun hideKeyBoardWithPinCode() {
        binding.pinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 6) {
                    hideKeyboard(requireContext(), requireView())
                    apiCheckInstallationRequest()
                }
            }
        })
    }

    private fun initData() {
        with(binding) {
            binding.topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.title.text = getString(R.string.checkout)
            cartId = arguments?.getString("cartId") ?: ""
            handleOnPressBackButton { navigateBack() }
            val spannableString = SpannableString(getString(R.string.installation_available_you_can_pay_this_amount_at_the_time_of_installation))
            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black)), 0, "Installation available".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            installationTv.text = spannableString

            refresh { onComplete ->
                apiCheckoutRequest(cartId)
                onComplete()
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }

            couponsLayout.setOnAnimatedClickListener {
                val fragment = OffersAndCouponsFragment {
                    apiCheckoutRequest(cartId)

                }
                val bundle = Bundle().apply {
                    putString("cartId", cartId)
                }
                fragment.arguments = bundle
                fragment.show(childFragmentManager, TAG)

            }

            change.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("type", "checkout")
                }
                navigateToScreen(R.id.myAddressFragment, bundle = bundle, navOptions = navOptions)
            }

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            exploreMore.setOnAnimatedClickListener {
                navigateToScreen(R.id.homeFragment, null, navOptions)
            }
            checkBtn.setOnAnimatedClickListener {
                if (pinCode.text.toString().trim().isEmpty()) {
                    requireActivity().toast(getString(R.string.please_enter_pin_code))
                } else {
                    apiCheckInstallationRequest()
                }
            }

            checkbox.setOnAnimatedClickListener {
                val product = checkOutResponse?.data?.cart
                if (product?.isClicked == 0) {
                    checkbox.setImageResource(R.drawable.check)
                    enterPinCodeLayout.visibility = View.VISIBLE
                    product.isClicked = 1
                } else {
                    checkbox.setImageResource(R.drawable.uncheck)
                    enterPinCodeLayout.visibility = View.GONE
                    product?.isClicked = 0
                }
            }

            payNowBtn.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("cartId", cartId)
                    putString("addressId", addressId)
                    putString("type", type)
                    putString("amount", finalAmount)
                }
                if (checkOutResponse?.data?.address?.id == null) {
                    requireActivity().toast(getString(R.string.please_add_an_address))
                } else {
                    navigateToScreen(R.id.paymentFragment, bundle = bundle, navOptions = navOptions)
                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            cartItemRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = checkoutOrderItemListAdapter
            }
        }
    }

    companion object {
        private var checkOutResponse: CheckOutResponse? = null
        private var checkInstallationResponse: CheckInstallationResponse? = null
        private var removeInstallationResponse: RemoveInstallationResponse? = null
    }

}