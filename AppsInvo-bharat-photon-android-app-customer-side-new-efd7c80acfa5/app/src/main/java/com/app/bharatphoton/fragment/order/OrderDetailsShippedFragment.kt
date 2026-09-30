package com.app.bharatphoton.fragment.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.activity.MainActivity
import com.app.bharatphoton.activity.PaymentResult
import com.app.bharatphoton.adapter.OrderDetailsListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopUpDialog
import com.app.bharatphoton.common.Utils.startPayment
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentOrderDetailsShippedBinding
import com.app.bharatphoton.fragment.review.AddReviewFragment
import com.app.bharatphoton.module.orderResponse.CancelOrderResponse
import com.app.bharatphoton.module.orderResponse.OrderDetailsResponse
import com.app.bharatphoton.module.payNowResponse.PayNowResponse
import com.app.bharatphoton.module.payNowResponse.VerifyPaymentResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartViewModule
import com.app.bharatphoton.viewModule.OrderViewModule
import com.app.bharatphoton.viewModule.PayNowViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class OrderDetailsShippedFragment : BaseFragment<FragmentOrderDetailsShippedBinding>() {


    private var amount = ""
    private var orderId = ""
    private var productId = ""
    private var productIdForAddReview = ""
    private val cartViewModule by viewModels<CartViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val orderDetailsListAdapter by lazy {
        OrderDetailsListAdapter { type, data ->
            when (type) {
                "trackOrder" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data.id)
                    }
                    navigateToScreen(
                        id = R.id.orderTrackingFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                "orderAgain" -> {
                    showLoader()
                    apiAddToCartRequest(deviceId = SharedData.deviceId, productId = data.productId ?: "", quantity = data.quantity ?: 0)
                }

                "uploadSitePhoto" -> {
                    val bundle = Bundle().apply {
                        putString("orderId", orderDetailsResponse?.data?.orderDetails?.id ?: "")
                        putString("installationId", orderDetailsResponse?.data?.installastionAddrress?.id ?: "")
                        putString("orderNo", orderDetailsResponse?.data?.orderDetails?.uuid ?: "")
                    }
                    navigateToScreen(R.id.uploadPhotoFragment, bundle, navOptions)
                }

                "addReview" -> {
                    val addReview = AddReviewFragment().apply {
                        arguments = Bundle().apply {
                            putString("productId", data.productId ?: "")
                        }
                    }
                    addReview.show(childFragmentManager, addReview.tag)
                }

                "cancelOrder" -> {
                    showPopUpDialog(context = requireContext(), message = "Are you sure you want to cancel this order!", onOk = {
                        if (orderDetailsResponse?.data?.orderDetails?.paymentMethod == "cod") {
                            apiCancelOrderRequest(type = 2, orderId = orderDetailsResponse?.data?.orderDetails?.id ?: "", productId = data.id ?: "")
                        } else {
                            apiCancelOrderRequest(type = 1, orderId = orderDetailsResponse?.data?.orderDetails?.id ?: "", productId = data.id ?: "")
                        }
                    })
                }
                else -> {
                    //
                }
            }
        }
    }

    private val payNowViewModule by viewModels<PayNowViewModule>()
    private val viewModule by viewModels<OrderViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.reOrder.postValue(null)
        viewModule.orderDetails.postValue(null)
        viewModule.cancelOrder.postValue(null)
        viewModule.verifyPayment.postValue(null)
        cartViewModule.addToCart.postValue(null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiOrderDetailsRequest(orderId)
        apiOrderDetailsResponse()
        apiCancelOrderResponse()
        apiVerifyPaymentResponse()
        setDataInAdapter()
        apiAddToCartResponse { addToCartResponse ->
            hideLoader()
            navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOrderDetailsShippedBinding = FragmentOrderDetailsShippedBinding.inflate(layoutInflater)

    private fun apiCancelOrderRequest(type: Int, orderId: String, productId: String) {
        val request = JsonObject().apply {
            addProperty("type", type)
            addProperty("orderId", orderId)
            addProperty("productId", productId)
        }
        viewModule.cancelOrder(request = request)
    }

    private fun apiCancelOrderResponse() {
        viewModule.cancelOrder.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                hideLoader()
                return@observe
            }

            when (response) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMsg = JSONObject(response.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMsg)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        cancelOrderResponse = Gson().fromJson(response.data, CancelOrderResponse::class.java)
                        if (cancelOrderResponse?.code == 1) {
                            apiOrderDetailsRequest(orderId)
                            requireActivity().toast(cancelOrderResponse?.message ?: "")
                        } else {
                            requireActivity().toast(cancelOrderResponse?.message ?: "")
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

    private fun apiOrderDetailsRequest(orderId: String) {
        viewModule.orderDetails(orderId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiOrderDetailsResponse() {
        viewModule.orderDetails.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                hideLoader()
                return@observe
            }

            when (response) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(response.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        orderDetailsResponse = Gson().fromJson(response.data, OrderDetailsResponse::class.java)

                        if (orderDetailsResponse?.code != 1) {
                            requireActivity().toast(orderDetailsResponse?.message.toString())
                            return@observe
                        }

                        orderDetailsResponse?.data?.let { data ->
                            with(binding) {
                                rootView.visibility = View.VISIBLE
                                orderIdTv.text = data.orderDetails?.uuid ?: ""
                                orderDetailsListAdapter.submitList(data.orderDetails?.products ?: emptyList())
                                totalPriceTv.text = formatPrice(data.orderDetails?.subTotal ?: 0.0, "INR")
                                tax.text = formatPrice(data.orderDetails?.taxAmount ?: 0.0, "INR")
                                subTotalTv.text = formatPrice(data.orderDetails?.finalTotalAmount ?: 0.0, "INR")
                                amount = data.orderDetails?.finalTotalAmount.toString()
                                couponsLayout.visibility = if (data.orderDetails?.coupons.isNullOrEmpty()) View.GONE else View.VISIBLE
                                discountPrice.text = formatPrice(data.orderDetails?.discountedAmount?.toDouble() ?: 0.0, "INR")

                                val shouldHidePayNow = data.orderDetails?.products?.any { it?.orderStatus == 3 } == true
                                payNowBtn.visibility = if (shouldHidePayNow || data.orderDetails?.paymentMethod != "cod" || data.orderDetails.finalTotalAmount == 0.0) {
                                    View.GONE
                                } else {
                                    View.VISIBLE
                                }

                                date.text = formatReadableDate(data.orderDetails?.createdAt ?: "")

                                address.text = """
                                ${data.orderDetails?.address ?: ""}
                                ${data.orderDetails?.state ?: ""}, ${data.orderDetails?.city ?: ""}
                                ${data.orderDetails?.zipCode ?: ""}
                                ${data.orderDetails?.countryCode ?: ""} ${data.orderDetails?.phoneNo ?: ""}
                            """.trimIndent()

                                installationAddressTv.visibility = if (data.installastionAddrress?.address != null) View.VISIBLE else View.GONE
                                notFound.visibility = if (data.installastionAddrress?.address != null) View.GONE else View.GONE
                                installationAddressTv.text = """
                                ${data.installastionAddrress?.address ?: ""}
                                ${data.installastionAddrress?.state ?: ""}, ${data.installastionAddrress?.city ?: ""}
                                ${data.installastionAddrress?.zipCode ?: ""}
                            """.trimIndent()
                            }

                            // Download Invoice/Receipt Logic
                            if (data.orderDetails?.invoiceUrl.isNullOrEmpty()) {
                                binding.downloadInvoice.text = "Download Receipt"
                                binding.downloadInvoice.setOnAnimatedClickListener {
                                    data.orderDetails?.receiptUrl?.substringAfterLast("/")
                                        ?.let { Utils.downloadFile(context = requireContext(), url = data.orderDetails.receiptUrl, fileName = it) }
                                }
                            } else {
                                binding.downloadInvoice.text = "Download Invoice"
                                binding.downloadInvoice.setOnAnimatedClickListener {
                                    data.orderDetails?.invoiceUrl?.substringAfterLast("/")
                                        ?.let { Utils.downloadFile(context = requireContext(), url = data.orderDetails.invoiceUrl, fileName = it) }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                else -> {
                    hideLoader()
                    "Unknown response type".mlog()
                }
            }
        }
    }

    private fun apiPayNowRequest(orderId: String) {
        val request = JsonObject().apply {
            addProperty("orderId", orderId)
        }
        payNowViewModule.payNowRequest(request)
    }

    private fun apiPayNowResponse() {
        payNowViewModule.payNow.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(response.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        payNowResponse = Gson().fromJson(response.data, PayNowResponse::class.java)
                        if (payNowResponse?.code == 1) {
                            val data = payNowResponse?.data
                            getListenerFromActivity()
                            startPayment(data?.razorpayOrderId ?: "", activity = requireActivity(), amount = data?.amountInPaisa.toString())
                        } else {
                            requireActivity().toast(payNowResponse?.message ?: "")
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

    private fun apiVerifyPaymentRequest(orderId: String, paymentId: String, signature: String) {
        val request = JsonObject().apply {
            addProperty("razorpay_order_id", orderId)
            addProperty("razorpay_payment_id", paymentId)
            addProperty("razorpay_signature", signature)
        }
        payNowViewModule.verifyOnlinePayment(request)
    }

    private fun apiVerifyPaymentResponse() {
        payNowViewModule.verifyOnlinePayment.observe(viewLifecycleOwner) {
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

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        verifyPaymentResponse = Gson().fromJson(it.data, VerifyPaymentResponse::class.java)
                        if (verifyPaymentResponse?.code == 1) {
//                            navigateToScreen(R.id.paymentSuccessFragment,null,navOptions)
                            MainActivity.paymentResultLiveData.postValue(null)
                            binding.payNowBtn.visibility = View.GONE

                        } else {
                            requireActivity().toast(verifyPaymentResponse?.message ?: "")
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

    private fun formatReadableDate(dateString: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")
        val desiredFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        val date: Date = originalFormat.parse(dateString) ?: return ""
        return desiredFormat.format(date)
    }

    private fun getListenerFromActivity() {
        MainActivity.paymentResultLiveData.observe(viewLifecycleOwner) { paymentResult ->
            paymentResult?.let {
                when (paymentResult) {
                    is PaymentResult.Success -> {
                        apiVerifyPaymentRequest(orderId = paymentResult.orderId ?: "", paymentId = paymentResult.paymentId ?: "", signature = paymentResult.signature ?: "")
                    }

                    is PaymentResult.Error -> {
//                    requireActivity().toast("Payment Failed")
                    }
                    else -> {
                        //
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
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
            refresh { onComplete ->
                apiOrderDetailsRequest(orderId)
                onComplete()
            }
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.order_details)
            handleOnPressBackButton { navigateBack() }

//            orderStatus = arguments?.getInt("orderStatus") ?: 0
            productId = arguments?.getString("productId") ?: ""
            orderId = arguments?.getString("orderId") ?: ""
            productIdForAddReview = arguments?.getString("productId") ?: ""

        }
    }

    private fun onClick() {
        with(binding) {

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            payNowBtn.setOnAnimatedClickListener {
                apiPayNowRequest(orderId = orderId)
                apiPayNowResponse()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            orderDetailsRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = orderDetailsListAdapter

            }
        }
    }

    companion object {
        private var orderDetailsResponse: OrderDetailsResponse? = null
        private var cancelOrderResponse: CancelOrderResponse? = null
        private var payNowResponse: PayNowResponse? = null
        private var verifyPaymentResponse: VerifyPaymentResponse? = null
    }

}