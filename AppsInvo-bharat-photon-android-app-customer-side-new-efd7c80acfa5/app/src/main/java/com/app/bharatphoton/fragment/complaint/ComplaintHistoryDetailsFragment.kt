package com.app.bharatphoton.fragment.complaint

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentComplaintHistoryDetailsBinding
import com.app.bharatphoton.module.complaintResponse.ComplaintHistoryDetailsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ComplaintViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ComplaintHistoryDetailsFragment : BaseFragment<FragmentComplaintHistoryDetailsBinding>() {

    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }
    private val viewModule by viewModels<ComplaintViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiComplaintHistoryDetailsResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComplaintHistoryDetailsBinding = FragmentComplaintHistoryDetailsBinding.inflate(layoutInflater)

    private fun List<TextView>.setValuesOrNA(values: List<String?>) {
        for (i in this.indices) {
            this[i].text = values.getOrNull(i)?.takeIf { it.isNotEmpty() && it.isNotBlank() } ?: "N/A"
        }
    }

    private fun apiComplaintHistoryDetailsRequest(id: String) {
        viewModule.complaintDetails(id)
    }

    @SuppressLint("SetTextI18n")
    private fun apiComplaintHistoryDetailsResponse() {
        viewModule.complainDetails.observe(viewLifecycleOwner) {
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
                        complaintHistoryDetailsResponse = Gson().fromJson(it.data, ComplaintHistoryDetailsResponse::class.java)

                        if (complaintHistoryDetailsResponse?.code != 1) {
                            requireActivity().toast(complaintHistoryDetailsResponse?.message ?: "")
                            return@observe
                        }

                        with(binding) {
                            binding.detailsLayout.visibility = View.VISIBLE
                            complaintHistoryDetailsResponse?.data?.let { data ->
                                val complaintDetails = data.complaintDetails
                                val resolvedDetails = data.resolvedDetails

                                // Load Product Image (if available)
                                complaintDetails?.productImage?.firstOrNull()?.url?.let { imageUrl ->
                                    Glide.with(requireActivity()).load(imageUrl).placeholder(R.drawable.placeholder).into(productImg)
                                }
                                if (data.complaintDetails?.complaintReason == "Any other reason") {
                                    complaintType.text = data.complaintDetails.description ?: ""
                                } else {
                                    complaintType.text = data.complaintDetails?.complaintReason ?: ""
                                }

                                // Set values for TextViews
                                val textViews = listOf(
                                    complaintDate, productName, categoryName, modelName, sinNo, resolveActionDateTv, complaintNo, remarkTv, customerNameTv, payment, attendedBy, status
                                )

                                val values = listOf(
                                    "Complaint Date : " + formatReadableDate(complaintDetails?.createdAt ?: "", SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())),
                                    complaintDetails?.productName ?: "",
                                    complaintDetails?.categoryName ?: "",
                                    "Model Name : ${complaintDetails?.modelNo ?: ""}",
                                    "SIN : ${complaintDetails?.sin?.uppercase(Locale.getDefault())}",
                                    resolvedDetails?.resolveAction,
                                    complaintDetails?.uuid ?: "",
                                    resolvedDetails?.remark ?: "",
                                    resolvedDetails?.customerName ?: "",
                                    formatPrice(price = resolvedDetails?.totalPayment ?: 0.0, currencyType = "INR"),
                                    "${complaintDetails?.staffFirstName ?: ""} ${complaintDetails?.staffLastName ?: ""}",
                                )

                                Glide.with(requireContext()).load(data.complaintDetails?.staffImage ?: "").placeholder(R.drawable.placeholder).into(staffProfileImg)

                                textViews.setValuesOrNA(values)

                                status.apply {
                                    text = when (complaintDetails?.complaintStatus ?: -1) {
                                        0 -> "Pending"
                                        else -> "Resolved"
                                    }
                                    setTextColor(resources.getColor(if (complaintDetails?.complaintStatus == 0) R.color.blue else R.color.Deep_Saffron))
                                }

                                // Resolved Date Layout visibility
                                resolvedDateLayout.visibility = if (resolvedDetails?.resolveDate != null) {
                                    binding.resolveDateTv.text = formatReadableDate(
                                        dateString = resolvedDetails.resolveDate, simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    )
                                    View.VISIBLE
                                } else View.GONE
                            }
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
            binding.topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.title.text = getString(R.string.complaint_history_details)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.title.textSize = 16f
            topBar.topBarFunctionality.visibility = View.VISIBLE
            handleOnPressBackButton { navigateBack() }
            val id = arguments?.getString("productId")
            apiComplaintHistoryDetailsRequest(id ?: "")
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                notification.setOnAnimatedClickListener {
                    navigateToScreen(R.id.notificationFragment, null, navOptions)
                }
                cart.setOnAnimatedClickListener {
                    navigateToScreen(R.id.cartFragment, null, navOptions)
                }
            }

        }
    }

    companion object {
        private var complaintHistoryDetailsResponse: ComplaintHistoryDetailsResponse? = null
    }
}