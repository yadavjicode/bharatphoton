package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.RenewProductListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showDialog
import com.app.bharatphoton.databinding.RenewWarrantyProductListItemViewBinding
import com.app.bharatphoton.module.warrantyApiResponse.WarrantyListResponse.WarrantyListData
import java.util.Locale

class RenewProductListAdapter(val onClick: (WarrantyListData, String) -> Unit, private val onSelectionChanged: () -> Unit) : PagingDataAdapter<WarrantyListData, ViewHolder>(DiffUtil) {

    private val selectedPositions = mutableSetOf<String>()
    private val selectedSinList = mutableSetOf<String>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, warrantyList = snapshot().items) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RenewWarrantyProductListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun getSelectedSinList(): List<String> = selectedSinList.toList()

    fun clearSelection() {
        selectedPositions.clear()
        selectedSinList.clear()
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: RenewWarrantyProductListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(data: WarrantyListData, warrantyList: List<WarrantyListData>) {
            with(binding) {
                categoryName.text = data.categoryName
                productName.text = data.productName
                if (data.sin != null) {
                    sinNoTv.text = "SIN : ${data.sin}"
                } else {
                    sinNoTv.text = "FIN : ${data.finNo?.uppercase(Locale.ROOT)}"
                }


                if (data.productImage?.isNotEmpty() == true) {
                    Glide.with(root.context).load(data.productImage.get(0)?.url).placeholder(R.drawable.placeholder).into(productImg)
                } else {
                    Glide.with(root.context).load(data.uninstalledProductImage).placeholder(R.drawable.placeholder).into(productImg)
                }

                orderId.text = "Order Id :${data.uuid}"
                if (data.remainingWarrantyDays != null) {
                    if (data.remainingWarrantyDays <= 0) {
                        expirePlanTxt.text = "Plan Expired"
                        expirePlanTxt.setTextColor(root.context.getColor(R.color.red))
                    } else {
                        expirePlanTxt.text = "Expiring in : ${data.remainingWarrantyDays} Days"
                    }
                }
                modelName.text = "Model Name : ${data.modelNo}"

                renewWarranty.setOnAnimatedClickListener {
                    if (data.remainingWarrantyDays!! <= 0) {
                        onClick(data, "renewWarranty")

                    } else {
                        showDialog(context = root.context, messageText = "You can renew warranty after ${data.remainingWarrantyDays} days")
                    }
                }

                selectedLayout.setOnAnimatedClickListener {
                    val itemId = data.id ?: return@setOnAnimatedClickListener

                    val sameOrderIdItems = warrantyList.filter { it.uuid == data.uuid }
                    val allSelectedAreExpired = sameOrderIdItems.all { it.remainingWarrantyDays!! <= 0 }

                    val selectedItems = selectedPositions.mapNotNull { id -> warrantyList.find { it.id == id } }
                    val selectedUuids = selectedItems.map { it.uuid }.toSet()
                    val isDifferentUuidSelected = selectedUuids.isNotEmpty() && selectedUuids.any { it != data.uuid }

                    val selectedProductIds = selectedItems.map { it.productId }.toSet()
                    val isDifferentProductSelected = selectedProductIds.isNotEmpty() && selectedProductIds.any { it != data.productId }

                    if (!allSelectedAreExpired) {
                        showDialog(root.context, "Only expired items can be selected for this Order ID")
                    } else if (isDifferentUuidSelected) {
                        showDialog(root.context, "You can only select items from the same Order ID.")
                    } else if (isDifferentProductSelected) {
                        showDialog(root.context, "Plan is different. Please select items with the same Product.")
                    } else {
                        val identifier = data.sin ?: data.finNo ?: ""

                        if (selectedPositions.contains(itemId)) {
                            selectedPositions.remove(itemId)
                            selectedSinList.remove(identifier)
                        } else {
                            onClick.invoke(data, "root")
                            selectedPositions.add(itemId)
                            selectedSinList.add(identifier)
                        }
                        onSelectionChanged()
                        notifyDataSetChanged()
                    }
                }

                if (selectedPositions.contains(data.id)) {
                    selectedLayout.setBackgroundResource(R.drawable.select_background_with_deep_safron_stroke)
                } else {
                    selectedLayout.setBackgroundResource(0)
                }
            }
        }
    }

    private object DiffUtil : ItemCallback<WarrantyListData>() {
        override fun areItemsTheSame(oldItem: WarrantyListData, newItem: WarrantyListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WarrantyListData, newItem: WarrantyListData): Boolean {
            return oldItem == newItem
        }
    }

}