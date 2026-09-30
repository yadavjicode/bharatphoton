package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.adapter.MyOrderAdapter.MyOrderViewHolder
import com.app.bharatphoton.common.GlideImageLoaderHelper
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.MyOrderItemViewBinding
import com.app.bharatphoton.module.orderResponse.OrderListingResponse.OrderListingData
import java.text.SimpleDateFormat
import java.util.Locale


class MyOrderAdapter(val onClick: (OrderListingData, String, String?) -> Unit) : PagingDataAdapter<OrderListingData, MyOrderViewHolder>(MyOrderDiffUtil) {
    inner class MyOrderViewHolder(private val binding: MyOrderItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var installationId  = ""
        @SuppressLint("SetTextI18n")
        fun bind(item: OrderListingData) {
            with(binding) {
                if (item.uuid != null) {
                    orderId.visibility = View.VISIBLE
                    orderId.text = "Order Id : ${item.uuid}"
                } else {
                    orderId.visibility = View.GONE
                }

                if (item.createdAt != null) {
                    dateTv.text = formatReadableDate(item.createdAt, SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()))
                }

                if (item.products?.isNotEmpty() == true) {
                    GlideImageLoaderHelper.glide(context = root.context, dataImage = item.products[0]?.image ?: "", progressBar = progressBar, imageView = productImg)
                }
                paymentModeTv.text = item.paymentMethod?.uppercase(Locale.getDefault())
                item.products?.let { products ->
                    if (products.isNotEmpty()) {
                        val firstProductName = products[0]?.productName
                        val moreProductsCount = products.size - 1

                        if (moreProductsCount > 0) {
                            productName.text = "$firstProductName +$moreProductsCount more"
                        } else {
                            productName.text = firstProductName
                        }
                    }
                }


                itemsTv.text = "${item.itemCount.toString()} Items,"
                val price = formatPrice(price = item.subTotal?.toDouble() ?: 0.0, currencyType = "INR")
                priceTv.text = price

                root.setOnAnimatedClickListener {
                    onClick(item, "product", null)
                }
                item.products?.forEach {
                    if (it?.orderStatus == 0 && it.isSitePhotoUploaded == 0 && it.installationPincode != null) {
                        uploadSitePhotoBtn.visibility = View.VISIBLE
                    } else {
                        uploadSitePhotoBtn.visibility = View.GONE
                    }
                }
                uploadSitePhotoBtn.setOnAnimatedClickListener {
                    item.products?.forEach {
                        installationId = it?.installationAddressId?:""
                    }
                    onClick(item, "uploadSitePhoto", installationId)
                }
            }
        }

    }

    private object MyOrderDiffUtil : DiffUtil.ItemCallback<OrderListingData>() {
        override fun areItemsTheSame(oldItem: OrderListingData, newItem: OrderListingData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderListingData, newItem: OrderListingData): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        return MyOrderViewHolder(
            MyOrderItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }

    }
}