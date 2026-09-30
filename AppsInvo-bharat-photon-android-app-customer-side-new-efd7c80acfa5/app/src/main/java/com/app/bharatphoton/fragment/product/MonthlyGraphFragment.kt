package com.app.bharatphoton.fragment.product

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.MonthlyGraphAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.ScreenShotHelper
import com.app.bharatphoton.common.ScreenShotHelper.Companion.showSaveNotification
import com.app.bharatphoton.common.SharedData.navigateBackScreen
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentMonthlyGraphBinding
import com.app.bharatphoton.module.reportResponse.MonthlyReportResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ReportsViewModule
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MonthlyGraphFragment : BaseFragment<FragmentMonthlyGraphBinding>() {
    private var orderId = ""
    private var productId = ""
    private val reportViewModule by viewModels<ReportsViewModule>()

    private val monthlyReportAdapter by lazy {
        MonthlyGraphAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiMonthlyReportRequest(orderId = orderId, productId = productId)
        apiMonthlyReportResponse()
        setDataInAdapter()
        handleOnPressBackButton {
            navigateBack()
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            dataTableRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = monthlyReportAdapter
            }
        }
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMonthlyGraphBinding = FragmentMonthlyGraphBinding.inflate(inflater, container, false)

    private fun apiMonthlyReportRequest(orderId: String, productId: String) {
        reportViewModule.monthlyReport(orderId = orderId, productId = productId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiMonthlyReportResponse() {
        reportViewModule.monthlyReport.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error<*> -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message", "N/A")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading<*> -> showLoader()
                is NetworkResult.Success<*> -> {
                    hideLoader()
                    try {
                        monthlyReportResponse = Gson().fromJson(it.data, MonthlyReportResponse::class.java)
                        with(binding) {
                            if (monthlyReportResponse?.code == 1) {
                                root.visibility = View.VISIBLE
                                val data = monthlyReportResponse?.data
                                val reportingDate = formatReadableDate(dateString = data?.reportingdate ?: "", simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()))
                                reportingDateTv.text = reportingDate
                                siteNameTv.text = data?.userDetails?.name ?: ""
                                val fromDate = formatReadableDate(dateString = data?.duration?.from ?: "", simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()))
                                val toDate = formatReadableDate(dateString = data?.duration?.to ?: "", simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()))
                                durationTv.text = "$fromDate - $toDate"
                                monthlyReportAdapter.submitList(data?.installationData)

                                setupBarChart(
                                    usageChart,
                                    floatArrayOf(data?.userReports?.totalUserCount?.sensor?.toFloat() ?: 0.0f, data?.userReports?.totalUserCount?.urinal?.toFloat() ?: 0.0f),
                                    getString(R.string.no_of_usage),
                                    resources.getColor(R.color.Deep_Saffron)
                                )

                                setupBarChart(
                                    waterSavedChart,
                                    floatArrayOf(data?.userReports?.totalWaterSaved?.sensor?.toFloat() ?: 0.0f, data?.userReports?.totalWaterSaved?.urinal?.toFloat() ?: 0.0f),
                                    getString(R.string.total_water_saved_in_litres),
                                    "#326DB1".toColorInt()
                                )

                                setupBarChart(
                                    costSavedChart,
                                    floatArrayOf(data?.userReports?.totalCostSaved?.sensor?.toFloat() ?: 0.0f, data?.userReports?.totalCostSaved?.urinal?.toFloat() ?: 0.0f),
                                    getString(R.string.total_cost_saved),
                                    "#0BA046".toColorInt()
                                )
                            } else {
                                requireActivity().toast(monthlyReportResponse?.message ?: "")
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
            navigateBackScreen = "SinDetailsScreen"
            orderId = arguments?.getString("orderId", "") ?: ""
            productId = arguments?.getString("productId", "") ?: ""

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClick() {
        with(binding) {
            downloadImg.setOnAnimatedClickListener {
                scrollView.isNestedScrollingEnabled = false
                val timestamp = System.currentTimeMillis()
                val file = File(context?.getExternalFilesDir(null), "Report_$timestamp.pdf")

                ScreenShotHelper.generateFullPDFAsync(
                    requireContext(), screenLayout, dataTableRv, file.name
                ) { file ->
                    if (file != null) {
                        Toast.makeText(requireContext(), "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                        showSaveNotification(requireContext(), file.absolutePath)
                    } else {
                        Toast.makeText(requireContext(), "Failed to generate PDF", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun setupBarChart(chart: BarChart, values: FloatArray, label: String, color: Int) {
        val entries = listOf(
            BarEntry(0f, values[0]), BarEntry(1f, values[1])
        )

        val dataSet = BarDataSet(entries, label).apply {
            setColor(color)
            valueTextSize = 8f
            setDrawValues(true)
        }

        val barData = BarData(dataSet).apply {
            barWidth = .8f
        }

        chart.apply {
            data = barData
            setFitBars(true)
            description.isEnabled = false
            axisRight.isEnabled = false
            legend.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(arrayOf("Sensor Tap", "Urinal Sensor"))
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                isGranularityEnabled = true
                setDrawGridLines(false)
                setAvoidFirstLastClipping(true)
                setCenterAxisLabels(false)
                labelCount = 2
            }

            axisLeft.axisMinimum = 0f

            animateY(1000)
            invalidate()
        }
    }

    companion object {
        private var monthlyReportResponse: MonthlyReportResponse? = null
    }

}