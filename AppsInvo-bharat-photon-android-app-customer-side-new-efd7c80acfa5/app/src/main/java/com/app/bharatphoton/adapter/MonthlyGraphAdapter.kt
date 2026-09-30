package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.MonthlyGraphAdapter.ViewHolder
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.databinding.ReportDataItemViewBinding
import com.app.bharatphoton.module.reportResponse.MonthlyReportResponse.Data.InstallationData
import java.text.SimpleDateFormat
import java.util.Locale

class MonthlyGraphAdapter : ListAdapter<InstallationData, ViewHolder>(DiffUtils) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ReportDataItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(private val binding: ReportDataItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: InstallationData, position: Int) {
            with(binding) {
                serialNoTv.text = (position + 1).toString()
                sinTv.text = data.sin ?: ""
                finTv.text = data.finNo ?: ""
                productNameTv.text = data.productName ?: ""
                modelNoTv.text = data.modelNo ?: ""
                val installationDate = Utils.formatReadableDate(dateString = data.resolveDate ?: "", simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                if (installationDate.isBlank()) installationDateTv.text = "N/A" else installationDateTv.text = installationDate


                batteryVoltageTv.text = "${data.batteryVoltage ?: ""} V"
                waterSavedTv.text = "${data.waterSaved ?: ""} Ltrs"
                costSavedTv.text = "${data.costSaved ?: ""} Rs"
                userCountTv.text = "${data.userCount ?: ""}"
                sensingRangeTv.text = (data.range ?: "N/A").toString()

                if (data.isUrinalSensorAvilable == 1) {
                    preflushTv.text = "Preflush in AF"
                    flushTimeTv.text = "Flush Time in AF"
                    offDetailTv.text = data.flushTime?.toString()
                    cutOffDetailTv.text = data.preflush?.toString()
                } else if (data.isFieldAvailable == 1) {
                    preflushTv.text = "Off delay in AT"
                    flushTimeTv.text = "Cutoff delay in AT"
                    offDetailTv.text = data.offDelay?.toString()
                    cutOffDetailTv.text = data.cutOffDelay?.toString()
                }
                val visibility = if (data.isFieldAvailable == 1 || data.isUrinalSensorAvilable == 1) View.VISIBLE else View.GONE

                listOf(
                    waterSavedLayout, costSavedLayout, userCountLayout, batteryVoltageLayout, sensingRangeLayout, preflushLayout, flushTimeLayout
                ).forEach { it.visibility = visibility }

                val endDate =
                    Utils.formatReadableDate(dateString = data.warrentyEndDate ?: data.subscriptionExpiryDate ?: "", simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                if (endDate.isBlank()) endDateTv.text = "N/A" else endDateTv.text = endDate


            }
        }
    }

    private object DiffUtils : DiffUtil.ItemCallback<InstallationData>() {
        override fun areItemsTheSame(oldItem: InstallationData, newItem: InstallationData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InstallationData, newItem: InstallationData): Boolean {
            return oldItem == newItem
        }
    }

}