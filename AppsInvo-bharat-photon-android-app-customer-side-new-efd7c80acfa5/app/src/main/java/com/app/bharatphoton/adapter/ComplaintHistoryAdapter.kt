package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.ComplaintHistoryItemViewBinding
import com.app.bharatphoton.module.complaintResponse.ComplaintHistoryResponse.ComplaintHistoryData
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class ComplaintHistoryAdapter(val onClick: (ComplaintHistoryData, String) -> Unit) : PagingDataAdapter<ComplaintHistoryData, ComplaintHistoryAdapter.ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: ComplaintHistoryItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ComplaintHistoryData) {
            with(binding) {
                complaintStatusTv.text = if (data.complaintStatus == 0) "Pending" else if (data.complaintStatus == 1) "In Process" else "Completed"
                complaintStatusTv.setTextColor(root.context.resources.getColor(if (data.complaintStatus == 0) R.color.blue else R.color.green))
                if (data.productImage?.isNotEmpty() == true) {
                    Glide.with(root.context).load(data.productImage[0]?.url).placeholder(R.drawable.placeholder).into(productImage)
                }
                val date = formatReadableDate(dateString = data.createdAt ?: "", SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()))
                complaintDate.text = formatText(prefix = "Complaint Date : ", plan = date, color = Color.BLACK)
                categoryName.text = data.categoryName
                if (data.productName != null) {
                    productName.text = data.productName
                } else {
                    productName.text = "Product Name : N/A"
                }
                if (data.modelNo != null) {
                    modelName.text = "Model Name : ${data.modelNo}"
                } else {
                    modelName.text = "Model Name : N/A"
                }
                if (data.sin != "") {
                    sinNo.text = "Sin No. : ${data.sin?.uppercase()}"
                } else {
                    sinNo.text = "Sin No. : N/A"
                }
                if (data.complaintReason == "Any other reason") {
                    issuesTv.text = data.description
                } else {
                    issuesTv.text = data.complaintReason
                }
                itemView.setOnAnimatedClickListener {
                    onClick(data, "root")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ComplaintHistoryItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ComplaintHistoryData>() {
        override fun areItemsTheSame(oldItem: ComplaintHistoryData, newItem: ComplaintHistoryData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ComplaintHistoryData, newItem: ComplaintHistoryData): Boolean {
            return oldItem == newItem
        }
    }

}