package com.app.bharatphoton.fragment.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.DateListAdapter
import com.app.bharatphoton.adapter.ServiceLogAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.downloadFile
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentMySubscriptionDetailsBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchasedSubscriptionDetailsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.PurchaseSubscriptionViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class MySubscriptionDetailsFragment : BaseFragment<FragmentMySubscriptionDetailsBinding>() {

    private var subscriptionId = ""
    private var sin = ""
    private var installationId = ""
    private val dateListAdapter by lazy {
        DateListAdapter { type, data ->
            when (type) {
                "root" -> {
                    apiSubscriptionServiceLogRequest(id = subscriptionId, date = data, sin = sin)
                }
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val serviceLogAdapter by lazy { ServiceLogAdapter() }
    private val viewModule by viewModels<PurchaseSubscriptionViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiPurchasedSubscriptionDetailsRequest(installationId = installationId)
        apiPurchasedSubscriptionDetailsResponse()
    }


    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMySubscriptionDetailsBinding = FragmentMySubscriptionDetailsBinding.inflate(layoutInflater)

    private fun apiPurchasedSubscriptionDetailsRequest(installationId: String) {
        viewModule.purchaseSubscriptionDetails(subscriptionId, type = 0, installationId = installationId)
    }

    private fun apiSubscriptionServiceLogRequest(id: String, date: String, sin: String) {
        lifecycleScope.launch {
            viewModule.subscriptionServiceLog(id = id, date = date, sin = sin).collectLatest { list ->
                serviceLogAdapter.submitData(list)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun apiPurchasedSubscriptionDetailsResponse() {
        viewModule.purchaseSubscriptionDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        requireActivity().toast(JSONObject(result.errorMessage ?: "").optString("message"))
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()

                is NetworkResult.Success -> {
                    hideLoader()
                    binding.mySubscriptionDetailsFragment.visibility = View.VISIBLE
                    try {
                        purchasedSubscriptionDetailsResponse = Gson().fromJson(result.data, PurchasedSubscriptionDetailsResponse::class.java)
                        purchasedSubscriptionDetailsResponse?.let { response ->
                            if (response.code == 1) {
                                with(binding) {
                                    RecyclerAnimationHelper.animation(requireContext(), root)
                                    response.data?.let { data ->
                                        // Load subscription details
                                        Glide.with(requireContext()).load(data.subscriptionLisitng?.productImage?:"").into(productImg)
                                        categoryName.text = data.subscriptionLisitng?.categoryName?:""
                                        productName.text = data.subscriptionLisitng?.productName?:""
                                        sin = data.sinDetails?.sin ?: ""
                                        modelName.text = "Model No. : ${data.subscriptionLisitng?.modelNo?:""}"
                                        quantity.text = "QTY : ${data.subscriptionLisitng?.quantity?:0}"
                                        status.text = "Status : "
                                        subscriptionId = data.subscriptionLisitng?.id ?: ""
                                        // Set visibility for views
                                        downloadReportTv.visibility = if (!data.subscriptionLisitng?.invoiceUrl.isNullOrEmpty()) View.VISIBLE else View.GONE

                                        setViewsVisibility(
                                            data.subscriptionLisitng?.isFieldAvailable == 1 || data.subscriptionLisitng?.isUrinalSensorAvilable == 1,
                                            waterSavedLayout,
                                            costSavedLayout,
                                            userCountLayout,
                                            batteryVoltageLayout,
                                            sensingRangeLayout,
                                            preflushLayout,
                                            flushTimeLayout
                                        )

                                        if (data.complaintDates?.isNotEmpty() == true) {
                                            apiSubscriptionServiceLogRequest(id = subscriptionId?:"", date = data.complaintDates[0] ?: "", sin = data.sinDetails?.sin ?: "")
                                            dateListAdapter.submitList(data.complaintDates)
                                        }

                                        if (data.complaintDates?.isNotEmpty() == true) serviceLogLayout.visibility = View.VISIBLE else serviceLogLayout.visibility = View.GONE

                                        if (data.subscriptionLisitng?.isUrinalSensorAvilable == 1) {
                                            preflushTextView.text = "Preflush in AF"
                                            flushtimeTv.text = "Flush Time in AF"
                                            preflushTv.text = data.sinDetails?.preflush?.toString()?:""
                                            flushTimeTv.text = data.sinDetails?.flushTime?.toString()?:""
                                        } else if (data.subscriptionLisitng?.isFieldAvailable == 1) {
                                            preflushTextView.text = "Off delay in AT"
                                            flushtimeTv.text = "Cutoff delay in AT"
                                            preflushTv.text = data.sinDetails?.offDelay?.toString()?:""
                                            flushTimeTv.text = data.sinDetails?.cutOffDelay?.toString()?:""
                                        }

                                        data.sinDetails?.let { sinDetails ->
                                            setTextView(waterSavedTv, "${sinDetails.waterSaved} L")
                                            setTextView(costSavedTv, sinDetails.costSaved?.let { Utils.formatPrice(it.toDouble(), "INR") })
                                            setTextView(userCountTv, sinDetails.userCount)
                                            setTextView(batteryVoltageTv, "${sinDetails.batteryVoltage} V")
                                            setTextView(sensingRangeTv, sinDetails.range)
                                            customerNameTv.text = sinDetails.customerName?:""
                                            customerRemarkTv.text = sinDetails.remark?:""
                                        }

                                        // Installation address
                                        data.installationAddress?.let { address ->
                                            AddTv.visibility = View.GONE
                                            installationAddressTv.text = "${address.address?:""} \n${address.state?:""}, ${address.city?:""} \n${address.zipCode?:""}"
                                        } ?: run {
                                            notFound.visibility = View.VISIBLE
                                            AddTv.visibility = View.VISIBLE
                                            installationAddressTv.visibility = View.GONE
                                        }

                                        // Subscription dates
                                        data.subscriptionLisitng?.let { listing ->
                                            planStartDate.visibility = if (listing.subscriptionStartDate == null) View.GONE else View.VISIBLE
                                            planEndDate.visibility = if (listing.subscriptionExpiryDate == null) View.GONE else View.VISIBLE
                                            planStartDate.text =
                                                "Plan Start Date : ${formatReadableDate(listing.subscriptionStartDate ?: "", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))}"
                                            planEndDate.text =
                                                "Plan End Date   : ${formatReadableDate(listing.subscriptionExpiryDate ?: "", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))}"
                                        }

                                        // SIN number
                                        sinNo.text = data.subscriptionLisitng?.sin ?: ""
                                        sinNo.visibility = if (sinNo.text.isEmpty()) View.GONE else View.VISIBLE
                                    }
                                }
                            } else {
                                requireActivity().toast(response.message.toString())
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
            subscriptionId = arguments?.getString("subscriptionId","") ?: ""
            installationId = arguments?.getString("installationId","") ?: ""
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.title.text = getString(R.string.my_subscription)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }

            AddTv.setOnAnimatedClickListener {
                val data = purchasedSubscriptionDetailsResponse?.data

                val bundle = Bundle().apply {
                    putString("id", data?.subscriptionLisitng?.id)
                    putString("orderNo", data?.subscriptionLisitng?.uuid)
                    putString("subscribeList", "subscribeList")
                }
                navigateToScreen(R.id.confirmOrderFragment, bundle, navOptions)
            }

            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, null, navOptions)
            }

            downloadReportTv.setOnAnimatedClickListener {
                purchasedSubscriptionDetailsResponse?.data?.subscriptionLisitng?.invoiceUrl?.substringAfterLast("/")?.let {
                    downloadFile(
                        context = requireContext(), url = purchasedSubscriptionDetailsResponse?.data?.sinDetails?.montlyInstallationUrl ?: "", fileName = it
                    )
                }
            }

            registerComplaint.setOnAnimatedClickListener {
                val data = purchasedSubscriptionDetailsResponse?.data
                val bundle = Bundle().apply {
                    putString("productId", data?.subscriptionLisitng?.productId)
                    putString("productName", data?.subscriptionLisitng?.productName)
                    putString("categoryName", data?.subscriptionLisitng?.categoryName)
                    putString("categoryId", data?.subscriptionLisitng?.categoryId)
                    putString("sin", data?.sinDetails?.sin)
                    putString("subscriptionId", data?.subscriptionLisitng?.id)
                    putInt("complaintType", 2)
                }
                navigateToScreen(
                    id = R.id.complaintRegisterFragment, bundle = bundle, navOptions = navOptions
                )
            }
            renewPlanBtn.setOnAnimatedClickListener {
                try {
                    val data = purchasedSubscriptionDetailsResponse?.data?.subscriptionLisitng?.daysRemaining
                    if (data != null) {
                        if (data <= 1) {
                            val sinList = mutableListOf<String>()
                            purchasedSubscriptionDetailsResponse?.data?.sinDetails?.sin?.let { sinList.add(it) }
                            val sinParam = sinList.joinToString(",") { it.trim() }
                            val bundle = Bundle().apply {
                                putString("subscriptionId", purchasedSubscriptionDetailsResponse?.data?.subscriptionLisitng?.id ?: "")
                                putString("sinList", sinParam)
                            }
                            navigateToScreen(
                                id = R.id.renewPlanFragment, bundle = bundle, navOptions = navOptions
                            )
                        } else {
                            requireActivity().toast("You can renew plan after $data days")
                        }
                    } else {/* val sinList = mutableListOf<String>()
                        purchasedSubscriptionDetailsResponse?.data?.sinDetails?.sin?.let { sinList.add(it) }
                        val sinParam = sinList.joinToString(",") { it.trim() }
                        val bundle = Bundle().apply {
                            putString("subscriptionId", purchasedSubscriptionDetailsResponse?.data?.subscriptionLisitng?.id)
                            putString("sinList", sinParam)
                        }
                        navigateToScreen(
                            id = R.id.renewPlanFragment, bundle = bundle, navOptions = navOptions
                        )*/
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            serviceLogRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = serviceLogAdapter
            }

            dateListRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = dateListAdapter

            }
        }
    }

    // Set text views with data or "N/A"
    private fun setTextView(textView: TextView, value: Any?) {
        textView.text = value?.toString() ?: "N/A"
    }

    private fun setViewsVisibility(isVisible: Boolean, vararg views: View) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        views.forEach { it.visibility = visibility }
    }

    companion object {
        private var purchasedSubscriptionDetailsResponse: PurchasedSubscriptionDetailsResponse? = null
    }
}