package com.app.bharatphoton.fragment.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.zoomInOutAnimation
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentOrderTrackingBinding
import com.app.bharatphoton.module.orderResponse.OrderTrackingResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.OrderViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Suppress("DEPRECATION")
@AndroidEntryPoint
class OrderTrackingFragment : BaseFragment<FragmentOrderTrackingBinding>() {
    private var productId = ""

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<OrderViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiOrderDetailsRequest(productId)
        apiOrderDetailsResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOrderTrackingBinding = FragmentOrderTrackingBinding.inflate(layoutInflater)

    private fun apiOrderDetailsRequest(productId: String) {
        viewModule.orderTrack(productId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiOrderDetailsResponse() {
        viewModule.orderTrack.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(response.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        orderTrackingResponse = Gson().fromJson(response.data, OrderTrackingResponse::class.java)
                        if (orderTrackingResponse?.code == 1) {
                            with(binding) {
                                val data = orderTrackingResponse?.data ?: return@with
                                orderTrackingLayout.visibility = View.VISIBLE
                                val availableDate = data.product?.pendingProductDate
                                    ?: data.product?.processedProductDate
                                    ?: data.product?.dispatchedProductDate
                                    ?: data.product?.deliveredProductDate

                                amount.text = formatPrice(data.product?.price?.toDouble() ?: 0.0, "INR")
                                updateDateView(orderReceivedTimeTv, data.product?.pendingProductDate, "dd MMM, yyyy")
                                updateDateView(orderDateTv, availableDate )
                                updateDateView(orderDispatchedTimeTv, data.product?.dispatchedProductDate, "hh:mm a")
                                updateDateView(orderDeliveredTimeTv, data.product?.deliveredProductDate, "hh:mm a")
                                updateDateView(orderProcessedTimeTv, data.product?.processedProductDate, "hh:mm a")
                                orderId.text = data.uuid
                                addressTv.text = "${data.address?.zipCode}, ${data.address?.address}, ${data.address?.city}, ${data.address?.state}"
                                updateOrderStatus(data.product?.orderStatus)
                            }
                        } else {
                            requireActivity().toast(orderTrackingResponse?.message.orEmpty())
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

    override fun onDestroyView() {
        super.onDestroyView()

    }
    private fun formatReadableDate1(dateString: String, simpleDateFormat: SimpleDateFormat): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date = originalFormat.parse(dateString) ?: return ""
            simpleDateFormat.format(date)
        } catch (e: Exception) {
            "Date parsing failed: ${e.message}".mlog()
            ""
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
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.order_tracking)
            handleOnPressBackButton {
                navigateBack()
            }
            productId = arguments?.getString("productId") ?: ""
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            contactNowBtn.setOnAnimatedClickListener {
                openDialer()
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


    private fun updateDateView(textView: TextView, date: String?, format: String = "dd EEEE, MMMM yyyy") {
        if (!date.isNullOrEmpty()) {
            try {
                val formattedDate = formatReadableDate1(date, SimpleDateFormat(format, Locale.getDefault()))
                textView.text = formattedDate
                textView.visibility = View.VISIBLE
            } catch (e: Exception) {
                "${textView.id} Date Error: ${e.message}".mlog()
                textView.visibility = View.GONE
            }
        } else {
            textView.visibility = View.GONE
        }
    }


    private fun updateOrderStatus(statusCode: Int?) {
        with(binding) {
            when (statusCode) {
                0 -> updateStatusUI(getString(R.string.pending), R.color.light_pink200, R.color.pink, listOf(pendingImg))
                1 -> updateStatusUI(getString(R.string.processing), R.color.Light_Deep_Saffron, R.color.Deep_Saffron, listOf(pendingImg, processImg))
                2 -> updateStatusUI(getString(R.string.dispatched), R.color.light_blue, R.color.blue, listOf(pendingImg, processImg, dispatchImg))
                3 -> updateStatusUI(getString(R.string.delivered), R.color.light_green, R.color.green, listOf(pendingImg, processImg, dispatchImg, deliveredImg))
                else -> {
                    //
                }
            }
        }
    }

    private fun updateStatusUI(statusText: String, bgColor: Int, textColor: Int, activeImages: List<ImageView>) {
        with(binding) {
            statusTv.text = statusText
            status.setCardBackgroundColor(requireContext().getColor(bgColor))
            statusTv.setTextColor(resources.getColor(textColor))
            activeImages.forEach { it.setImageResource(R.drawable.check_) }
            zoomInOutAnimation(activeImages)
        }
    }

    companion object {
        private var orderTrackingResponse: OrderTrackingResponse? = null
    }
}