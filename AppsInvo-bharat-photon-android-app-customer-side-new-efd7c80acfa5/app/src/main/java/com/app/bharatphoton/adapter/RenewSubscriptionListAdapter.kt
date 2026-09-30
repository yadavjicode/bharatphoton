package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.databinding.RenewWarrantyProductListItemViewBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPurchaseSubscriptionListResponse.RenewPurchaseSubscriptionData
import com.bumptech.glide.Glide

class RenewSubscriptionListAdapter(val onClick: (RenewPurchaseSubscriptionData, String) -> Unit, private val onSelectionChanged: () -> Unit) :
    PagingDataAdapter<RenewPurchaseSubscriptionData, RenewSubscriptionListAdapter.ViewHolder>(DiffUtil) {

    private val selectedPositions = mutableSetOf<Int>()
    private val selectedSinList = mutableSetOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, snapshot().items) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RenewWarrantyProductListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: RenewWarrantyProductListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bind(data: RenewPurchaseSubscriptionData, warrantyList: List<RenewPurchaseSubscriptionData>) {
            with(binding) {
                orderId.text = "Order Id: ${data.subscriptionuuid}"
                categoryName.text = data.categoryName
                productName.text = data.productName
                renewWarranty.text = "Re-new Subscription"
                modelName.text = "Model No. : ${data.modelNo}"

                sinNoTv.text = if (data.sin != null) "SIN : ${data.sin}" else "SIN : N/A"

                if (data.productImage?.isNotEmpty() == true) {
                    Glide.with(root.context).load(data.productImage[0]?.url).placeholder(R.drawable.placeholder).into(productImg)
                }


                renewWarranty.setOnAnimatedClickListener {
                    if (data.remainingDays!! <= 0) {
                        onClick(data, "renewWarranty")
                    } else {
                        showDialog(root.context, "You can renew subscription after ${data.remainingDays} Days")
                    }
                }

                expirePlanTxt.text = if (data.remainingDays!! <= 0) "Expired" else "Expiring in : ${data.remainingDays} Days"
                expirePlanTxt.setTextColor(if (data.remainingDays <= 0) Color.RED else Color.BLACK)

                selectedLayout.setOnAnimatedClickListener {
                    val sameOrderIdItems = warrantyList.filter { it.subscriptionuuid == data.subscriptionuuid }
                    val allSelectedAreExpired = sameOrderIdItems.all { data.remainingDays <= 0 }
                    val selectedItems = selectedPositions.mapNotNull { index -> getItem(index) }
                    val selectedUuids = selectedItems.map { it.subscriptionuuid }.toSet()
                    val isDifferentUuidSelected = selectedUuids.isNotEmpty() && selectedUuids.any { it != data.subscriptionuuid }
                    if (!allSelectedAreExpired) {
                        showDialog(root.context, "Only expired items can be selected for this Order ID")
                    } else if (isDifferentUuidSelected) {
                        showDialog(root.context, "You can only select items from the same Order ID.")
                    } else {
                        if (selectedPositions.contains(bindingAdapterPosition)) {
                            selectedPositions.remove(bindingAdapterPosition)
                            selectedSinList.remove(data.sin ?: "")
                        } else {
                            onClick.invoke(data, "root")
                            selectedPositions.add(bindingAdapterPosition)
                            selectedSinList.add(data.sin ?: "")
                        }
                        notifyItemChanged(bindingAdapterPosition)
                        onSelectionChanged()
                    }
                }
                selectedLayout.setBackgroundResource(
                    if (selectedPositions.contains(layoutPosition)) R.drawable.select_background_with_deep_safron_stroke
                    else 0
                )
            }
        }

    }

    fun getSelectedSinList(): List<String> = selectedSinList.toList()

    private object DiffUtil : ItemCallback<RenewPurchaseSubscriptionData>() {
        override fun areItemsTheSame(oldItem: RenewPurchaseSubscriptionData, newItem: RenewPurchaseSubscriptionData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RenewPurchaseSubscriptionData, newItem: RenewPurchaseSubscriptionData): Boolean {
            return oldItem == newItem
        }
    }
}
