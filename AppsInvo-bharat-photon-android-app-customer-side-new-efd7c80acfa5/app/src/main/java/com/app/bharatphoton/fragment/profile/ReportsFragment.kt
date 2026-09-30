package com.app.bharatphoton.fragment.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.CostSavedAdapter
import com.app.bharatphoton.adapter.UserCountAdapter
import com.app.bharatphoton.adapter.WaterReportAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.ShowDownloadNotificationHelper.Companion.downloadAllGraphsAsPDF
import com.app.bharatphoton.common.ShowDownloadNotificationHelper.Companion.downloadGraphAsPDF
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentReportsBinding
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.module.reportResponse.ReportsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ReportsViewModule
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>() {
    private val waterReportList = mutableListOf<Module>()
    private val waterReportAdapter by lazy {
        WaterReportAdapter()
    }
    private val viewModule by viewModels<ReportsViewModule>()
    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }
    private val userCountAdapter by lazy {
        UserCountAdapter()
    }
    private val costSavedAdapter by lazy {
        CostSavedAdapter()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentReportsBinding = FragmentReportsBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiReportsRequest()
        apiReportsResponse()
    }

    private fun setDataInAdapter() {
        with(binding) {
            /*     waterReportRv.apply {
                     layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                     adapter = waterReportAdapter
                 }

                 userCountRv.apply {
                     layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                     adapter = userCountAdapter
                 }

                 costSavedRv.apply {
                     layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                     adapter = costSavedAdapter
                 }*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        waterReportList.clear()
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            val costSaved = formatPrice(price = reportsResponse?.data?.costReport?.totalCostSavedForYear?.toDouble() ?: 0.0, currencyType = "INR")

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions = navOptions)
            }

            download.setOnAnimatedClickListener {
                downloadGraphAsPDF(
                    graphView = waterReportLineChart,
                    fileName = "waterReports",
                    topText = "Water Report",
                    bottomText = "Total Water Saved : ${reportsResponse?.data?.waterSavedReports?.totalWaterSavedForYear.toString()}"
                )
            }
            userCountDownload.setOnAnimatedClickListener {
                downloadGraphAsPDF(
                    graphView = userCountLineChart,
                    fileName = "userCountReports",
                    topText = "User Count",
                    bottomText = "User Count : ${reportsResponse?.data?.userCountReport?.totalUserCountForYear ?: 0}"
                )
            }
            costSavedDownload.setOnAnimatedClickListener {
                downloadGraphAsPDF(
                    graphView = costSavedLineChart, fileName = "costSavedReports", topText = "Cost Saved", bottomText = "Total Cost Saved : $costSaved"
                )
            }
            downloadAllBtn.setOnAnimatedClickListener {
                downloadAllGraphsAsPDF(
                    graphViews = listOf(waterReportLineChart, userCountLineChart, costSavedLineChart),
                    fileName = "allSavedReports",
                    topTexts = listOf("Water Reports", "User Count", "Cost Saved"),
                    bottomTexts = listOf(
                        "Total Water Saved : ${reportsResponse?.data?.waterSavedReports?.totalWaterSavedForYear.toString()}",
                        "User Count : ${reportsResponse?.data?.userCountReport?.totalUserCountForYear ?: 0}",
                        "Total Cost Saved : $costSaved"
                    )
                )
            }

            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, null, navOptions)
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.reports)
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            handleOnPressBackButton { navigateBack() }
            waterReportList.add(Module(id = 9, name = "Daily", image = 0))
            waterReportList.add(Module(id = 9, name = "Weekly", image = 0))
            waterReportList.add(Module(id = 9, name = "Monthly", image = 0))
            waterReportAdapter.submitList(waterReportList)
            userCountAdapter.submitList(waterReportList)
            costSavedAdapter.submitList(waterReportList)
        }
    }

    private fun <T> parseMonthlyData(monthlyData: List<T>, valueExtractor: (T) -> Float): ArrayList<Entry> {
        return monthlyData.mapIndexed { index, data ->
            Entry(index.toFloat(), valueExtractor(data))
        }.toCollection(ArrayList())
    }

    private fun waterReportLineChart(lineCharts: List<LineChart>, response: ReportsResponse) {
        with(binding) {
            val datasets = listOf(
                createDataset(
                    entries = parseMonthlyData(
                        response.data?.waterSavedReports?.monthlyWaterSaved?.filterNotNull() ?: emptyList()
                    ) { it.totalWaterSaved!!.toFloat() }, label = "", gradientColors = intArrayOf(
                        Color.parseColor("#FF5722"), Color.TRANSPARENT
                    )
                ), createDataset(
                    entries = parseMonthlyData(
                        response.data?.userCountReport?.monthlyUserCount?.filterNotNull() ?: emptyList()
                    ) { it.totalUserCount!!.toFloat() }, label = "", gradientColors = intArrayOf(
                        Color.parseColor("#3F51B5"), Color.TRANSPARENT
                    )
                ), createDataset(
                    entries = parseMonthlyData(
                        response.data?.costReport?.monthlyCostSaved?.filterNotNull() ?: emptyList()
                    ) { it.totalCostSaved!!.toFloat() }, label = "", gradientColors = intArrayOf(
                        Color.parseColor("#009688"), Color.TRANSPARENT
                    )
                )
            )
            lineCharts.zip(datasets).forEach { (lineChart, dataset) ->
                val lineData = LineData(dataset)
                lineChart.data = lineData
                val maxValue = minOf(5000f, dataset.yMax + 500f)
                lineChart.apply {
                    description.text = ""
                    description.textColor = Color.GRAY
                    description.textSize = 9f
                    axisRight.isEnabled = false
                    axisLeft.setDrawGridLines(false)
                    axisLeft.axisMinimum = 0f
                    axisLeft.axisMaximum = maxValue
                    axisLeft.setDrawAxisLine(true)
                    axisLeft.textColor = Color.BLACK
                    axisLeft.textSize = 8f
                    xAxis.setDrawGridLines(false)
                    xAxis.setDrawAxisLine(true)
                    xAxis.textColor = Color.BLACK
                    xAxis.textSize = 9f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = IndexAxisValueFormatter(getMonths())
                    xAxis.labelCount = getMonths().size
                    xAxis.granularity = 1f
                    xAxis.labelRotationAngle = -45f
//                    xAxis.setLabelCount(6, true)
                    xAxis.isGranularityEnabled = true
                    animateXY(1500, 1500)
                    invalidate()
                }
            }
        }
    }

    private fun getMonths(): List<String> {
        return listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
    }

    private fun apiReportsRequest() {
        viewModule.reports()
    }

    @SuppressLint("SetTextI18n")
    private fun apiReportsResponse() {
        viewModule.report.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
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
                        reportsResponse = Gson().fromJson(it.data, ReportsResponse::class.java)
                        if (reportsResponse?.code == 1) {
                            binding.reportFragment.visibility = View.VISIBLE
                            reportsResponse?.let { waterReportLineChart(listOf(binding.waterReportLineChart, binding.userCountLineChart, binding.costSavedLineChart), response = it) }
                            val data = reportsResponse?.data
                            with(binding) {
                                val costSaved = formatPrice(price = data?.costReport?.totalCostSavedForYear?.toDouble() ?: 0.0, currencyType = "INR")
                                totalCostSavedTv.text = "Total Cost Saved : $costSaved"
                                val userCount = "User Count : ${data?.userCountReport?.totalUserCountForYear ?: 0}"
                                totalUserCountTv.text = userCount
                                totalWaterSavedTv.text = "Total Water Saved : ${data?.waterSavedReports?.totalWaterSavedForYear.toString()} Ltr"

                            }
                        } else {
                            requireActivity().toast(reportsResponse?.message ?: "")
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

    private fun createDataset(
        entries: ArrayList<Entry>, label: String, gradientColors: IntArray
    ): LineDataSet {
        return LineDataSet(entries, label).apply {
            color = Color.BLACK
            valueTextColor = Color.BLACK
            valueTextSize = 8f
            lineWidth = 2f
            setCircleColor(Color.RED)
            circleRadius = 5f
//            setDrawValues(false)
            setDrawFilled(true)
            fillDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, gradientColors
            ).apply {
                shape = GradientDrawable.RECTANGLE
            }
        }
    }

    companion object {
        private var reportsResponse: ReportsResponse? = null
    }
}