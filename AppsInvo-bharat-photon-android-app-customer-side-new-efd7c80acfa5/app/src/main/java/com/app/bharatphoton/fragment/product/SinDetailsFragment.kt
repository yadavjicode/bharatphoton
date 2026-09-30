package com.app.bharatphoton.fragment.product

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.DateListAdapter
import com.app.bharatphoton.adapter.ServiceLogSinDetailsAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.cartItemCount
import com.app.bharatphoton.common.SharedData.navigateBackScreen
import com.app.bharatphoton.common.SharedData.sinDetailsServiceLogRemainingWarrantyDays
import com.app.bharatphoton.common.Utils.downloadFile
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSinDetailsBinding
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.module.productResponse.SinDetailsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ProductViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class SinDetailsFragment : BaseFragment<FragmentSinDetailsBinding>() {

    private var installationId = ""
    private var orderId = ""
    private var productId = ""
    private var sin = ""

    private val dateListAdapter by lazy {
        DateListAdapter { type, data ->
            when (type) {
                "root" -> {
                    apiServiceLogRequest(
                        orderId = sinDetailsResponse?.data?.installatedDetails?.orderId ?: "",
                        date = data,
                        productId = sinDetailsResponse?.data?.installatedDetails?.productId ?: "",
                        sin = sin
                    )
                }
            }
        }
    }

    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val serviceLogAdapter by lazy { ServiceLogSinDetailsAdapter() }
    private val viewModule by viewModels<ProductViewModule>()
    private val dateList = mutableListOf<Module>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiSinDetailsRequest()
        setDataInAdapter()
        apiSinDetailsResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSinDetailsBinding = FragmentSinDetailsBinding.inflate(layoutInflater)

    private fun apiServiceLogRequest(orderId: String, date: String, productId: String, sin: String) {
        lifecycleScope.launch {
            viewModule.sinDetailsServiceLog(orderId = orderId, date = date, productId = productId, sin = sin).collectLatest { list ->
                serviceLogAdapter.submitData(list)
            }
        }
    }

    private fun apiSinDetailsRequest() {
        viewModule.sinDetails(orderId = orderId, productId = productId, installationId = installationId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiSinDetailsResponse() {
        viewModule.sinDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(result.errorMessage ?: "").optString("message", "N/A")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message?.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        sinDetailsResponse = Gson().fromJson(result.data, SinDetailsResponse::class.java)
                        if (sinDetailsResponse?.code != 1) {
                            requireActivity().toast(sinDetailsResponse?.message ?: "N/A")
                            return@observe
                        }

                        with(binding) {
                            RecyclerAnimationHelper.animation(requireContext(), root)
                            sinDetailsFragment.visibility = View.VISIBLE

                            sinDetailsResponse?.data?.installatedDetails?.let { data ->
                                Glide.with(requireContext()).load(data.productImage?.firstOrNull()?.url).into(productImg)

                                downloadReportTv.visibility = if (data.sin != null) View.VISIBLE else View.GONE
                                sin = data.sin ?: "N/A"
                                categoryName.text = data.categoryName ?: "N/A"
                                productName.text = data.productName ?: "N/A"
                                modelName.text = "Model Name : ${data.modelNo ?: "N/A"}"

                                sinDetailsResponse?.data?.complaintDates?.firstOrNull()?.let { firstDate ->
                                    apiServiceLogRequest(
                                        orderId = data.orderId ?: "N/A", date = firstDate, productId = data.productId ?: "N/A", sin = data.sin ?: "N/A"
                                    )
                                    dateListAdapter.submitList(sinDetailsResponse?.data?.complaintDates)
                                    serviceLogLayout.visibility = View.VISIBLE

                                } ?: run {
                                    serviceLogLayout.visibility = View.GONE
                                }

                                sinDetailsServiceLogRemainingWarrantyDays = sinDetailsResponse?.data?.installatedDetails?.remainingWarrantyDays ?: 0

                                val visibility = if (data.isFieldAvailable == 1 || data.isUrinalSensorAvilable == 1) View.VISIBLE else View.GONE

                                if (data.isUrinalSensorAvilable == 1) {
                                    preflushTv.text = getString(R.string.preflush_in_af)
                                    flushTimeTv.text = getString(R.string.flush_time_in_af)
                                    flushTime.text = data.flushTime?.toString()
                                    preFlush.text = data.preflush?.toString()
                                } else if (data.isFieldAvailable == 1) {
                                    preflushTv.text = getString(R.string.off_delay_in_at)
                                    flushTimeTv.text = getString(R.string.cutoff_delay_in_at)
                                    flushTime.text = data.cutOffDelay?.toString()
                                    preFlush.text = data.offDelay?.toString()
                                }

                                listOf(
                                    waterSavedLayout, costSavedLayout, userCountLayout, batteryVoltageLayout, sensingRangeLayout, preflushLayout, flushTimeLayout
                                ).forEach { it.visibility = visibility }


                                planStartDate.text = "Warranty Start Date : ${formatReadableDate(data.warrentyStartDate ?: "", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))}"
                                planEndDate.text = "Warranty End Date   : ${formatReadableDate(data.warrentyEndDate ?: "", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))}"
                                sinNo.text = "SIN. : ${data.sin ?: "N/A"}"
                                customerNameTv.text = data.customerName ?: "N/A"
                                customerRemarkTv.text = data.remark ?: "N/A"
                                waterSavedTv.text = "${data.waterSaved ?: "0"} Ltrs"
                                costSavedTv.text = formatPrice(data.costSaved ?: 0.0, "INR")
                                sensingRange.text = data.range?.toString() ?: "N/A"
                                batteryVoltage.text = "${data.batteryVoltage?.toString()}V" ?: "N/A"
                                userCountTv.text = data.userCount?.toString() ?: "N/A"
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.mlog()
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
            topBar.cartItemCount.text = cartItemCount.toString()
            topBar.title.text = getString(R.string.sin_details)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            handleOnPressBackButton {
                navigateBackScreen = "SinDetailsScreen"
                navigateBack()
            }
            orderId = arguments?.getString("orderId","") ?: ""
            productId = arguments?.getString("productId","") ?: ""
            installationId = arguments?.getString("id","") ?: ""
            RecyclerAnimationHelper.animation(requireContext(), root)

            repeat(5) {
                dateList.add(Module(id = 0, name = "12, Mar 2024", image = 0))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener {
                    navigateBackScreen = "SinDetailsScreen"
                    navigateBack()
                }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, null, navOptions) }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
                call.setOnAnimatedClickListener { openDialer() }
            }

            downloadReportTv.setOnAnimatedClickListener {
                downloadFile(
                    requireContext(), sinDetailsResponse?.data?.installatedDetails?.montlyInstallationUrl ?: "",
                    fileName = "Monthly Report"
                )
                /* generatePDFAsync(requireContext(), screenLayout, "WaterReport") { file ->
                     if (file != null) {
                         Toast.makeText(requireContext(), "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                         showSaveNotification(requireContext(), file.absolutePath)
                     } else {
                         Toast.makeText(requireContext(), "Failed to generate PDF", Toast.LENGTH_LONG).show()
                     }
                 }*/
            }
            renewPlanBtn.setOnAnimatedClickListener {
                if (sinDetailsResponse?.data?.installatedDetails?.remainingWarrantyDays != null) {
                    if (sinDetailsResponse?.data?.installatedDetails?.remainingWarrantyDays!! <= 1) {
                        val sinList = arrayListOf<String>()
                        sinDetailsResponse?.data?.installatedDetails?.finNo?.let { sinList.add(it) }
                        val bundle = Bundle().apply {
                            putString("ProductId", sinDetailsResponse?.data?.installatedDetails?.productId?:"")
                            putString("orderId", sinDetailsResponse?.data?.installatedDetails?.orderId?:"")
                            putStringArrayList("sinList", sinList)
                        }
                        navigateToScreen(
                            id = R.id.warrantyPlansFragment, bundle = bundle, navOptions = navOptions
                        )
                    } else {
                        requireActivity().toast("You can renew after ${sinDetailsResponse?.data?.installatedDetails?.remainingWarrantyDays?:""} days")
                    }
                } else {
                    requireActivity().toast(getString(R.string.installation_is_not_done_by_staff_so_you_can_t_renew_plan))
                }
            }
            registerComplaint.setOnAnimatedClickListener {
                if (sinDetailsResponse?.data?.installatedDetails?.sin == null) {
                    requireActivity().toast(getString(R.string.installation_is_not_done_by_staff_so_you_can_t_register_complaint))
                } else {
                    val data = sinDetailsResponse?.data?.installatedDetails
                    val bundle = Bundle().apply {
                        putString("productId", data?.productId?:"")
                        putString("productName", data?.productName?:"")
                        putString("categoryName", data?.categoryName?:"")
                        putString("categoryId", data?.categoryId?:"")
                        putString("orderId", data?.orderId?:"")
                        putString("sin", data?.sin?:"")
                        putInt("complaintType", 1)
                    }
                    navigateToScreen(
                        id = R.id.complaintRegisterFragment, bundle = bundle, navOptions = navOptions
                    )
                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            dateListRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = dateListAdapter
            }

            serviceLogRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = serviceLogAdapter
            }
        }
    }


    companion object {
        private var sinDetailsResponse: SinDetailsResponse? = null
    }
}