package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.MySubscriptionAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.MySubcriptionListItemViewBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchasedSubscriptionListResponse.SubscriptionListData
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchasedSubscriptionListResponse.SubscriptionListData.InstallationMetric
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class MySubscriptionAdapter(val onClick: (String, InstallationMetric, SubscriptionListData) -> Unit, val clickUploadPhoto: (String, SubscriptionListData, InstallationMetric) -> Unit) :
    PagingDataAdapter<SubscriptionListData, ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MySubcriptionListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    inner class ViewHolder(private val binding: MySubcriptionListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SubscriptionListData) {
            with(binding) {
                try {

                    val mySubscriptionSinListAdapter = MySubscriptionSinListAdapter(parentItem = item) { type, data, _ ->
                        when (type) {
                            "root" -> onClick("root", data, item)
                            "reNewPlan" -> onClick("reNewPlan", data, item)
                            "registerComplaint" -> clickUploadPhoto("registerComplaint", item, data)
                        }
                    }


                    Glide.with(root.context).load(item.productImage?:"").placeholder(R.drawable.placeholder).into(productImg)
                    categoryName.text = item.categoryName ?: ""
                    productName.text = item.productName ?: ""
                    paymentModeTv.text = item.paymentMethod?.uppercase()
                    price.text = "₹${item.price.toString()}/"
                    modelName.text = "Model Name : ${item.modelNo ?: ""}"
                    orderId.text = item.uuid?.let { formatText(prefix = "Order Id : ", plan = it, color = Color.BLACK) }
                    qtyTv.text = "QTY : ${item.quantity.toString()}"
                    val date = formatReadableDate(item.createdAt ?: "", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
                    planPurchasedDateTv.text = formatText(prefix = "Date : ", plan = date, color = Color.BLACK)
                    val price = formatPrice(price = item.finalTotalAmount ?: 0.0, currencyType = "INR")
                    priceTv.text = "Total Price : $price/Incl. GST"

                    if (item.totalWaterSaved != null) waterSavedTv.text = "${item.totalWaterSaved} Ltr \nTotal Water Saved"
                    else waterSavedTv.text = "0 Ltrs \nTotal Water Saved"
                    savedLayout.visibility = if (item.isFieldAvailable == 0 && item.isUrinalSensorAvilable == 0) View.GONE else View.VISIBLE
                    if (item.totalCostSaved != null) {
                        val costSaved = formatPrice(price = item.totalCostSaved.toDouble(), currencyType = "INR")
                        costSavedTv.text = "$costSaved \nTotal Cost Saved"
                    } else {
                        costSavedTv.text = "₹ 0 \nTotal Cost Saved"
                    }

                    uploadPhoto.visibility = if (item.isSitePhotoUploaded == 1) View.GONE else View.VISIBLE

                    mySubscriptionSinListAdapter.submitList(item.installationMetrics)
//                    notifyItemChanged(layoutPosition)

                    mySubscriptionSinListRv.apply {
                        layoutManager = LinearLayoutManager(root.context)
                        adapter = mySubscriptionSinListAdapter
                    }


                    when (item.mode) {
                        "M" -> {
                            perMonthTxt.text = "per month"
                        }

                        "H" -> {
                            perMonthTxt.text = "6 months"
                        }

                        "Q" -> {
                            perMonthTxt.text = "Quarterly"
                        }

                        "Y" -> {
                            perMonthTxt.text = "per year"
                        }
                    }

                    when (item.planType) {
                        0 -> planTitle.text = formatText("Plan : ", "Lite", Color.BLACK)
                        1 -> planTitle.text = formatText("Plan : ", "Plus", Color.BLACK)
                        2 -> planTitle.text = formatText("Plan : ", "Premium", Color.BLACK)
                    }

                } catch (e: Exception) {
                    e.message.mlog()
                }
                reportDownloadTv.setOnClickListener {
                    item.installationMetrics?.firstOrNull()?.let { metric ->
                        onClick("downloadReport", metric, item)
                    }
                }

                uploadPhoto.setOnAnimatedClickListener {
                    clickUploadPhoto(
                        "uploadPhoto", item, item.installationMetrics?.firstOrNull() ?: InstallationMetric(
                            costSaved = 0, finNo = "", id = "", sin = "", subscriptionStartDate = "", subscriptionExpiryDate = "", isPlanExpired = 0, waterSaved = 0
                        )
                    )

                }
            }
        }
    }

    private object DiffUtil : ItemCallback<SubscriptionListData>() {
        override fun areItemsTheSame(oldItem: SubscriptionListData, newItem: SubscriptionListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubscriptionListData, newItem: SubscriptionListData): Boolean {
            return oldItem == newItem
        }
    }
}