package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.databinding.MyOrderItemViewBinding
import com.app.bharatphoton.module.orderResponse.OrderListingResponse.OrderListingData.Product


class NestedOrderListAdapter(val onClick: (Product, String) -> Unit, private var selectedTab: Int) : ListAdapter<Product, NestedOrderListAdapter.ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: MyOrderItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Product) {
            /*with(binding) {
//                Glide.with(root.context).load(item.image).placeholder(R.drawable.placeholder).into(productImg)
//                productNameTv.text = item.productName

                if (item.orderStatus == 0) uploadSitePhoto.visibility = View.VISIBLE else uploadSitePhoto.visibility = View.GONE

                if (item.isSitePhotoUploaded == 0) {
                    uploadSitePhoto.text = "Upload Site Photo"
                } else {
                    uploadSitePhoto.text = "Site Photo Uploaded"
                }

                if (item.uuid != null) {
                    orderId.visibility = View.VISIBLE
                    orderId.text = "Order Id : ${item.uuid}"
                } else {
                    orderId.visibility = View.GONE
                }

                uploadSitePhoto.setOnAnimatedClickListener {
                    if (item.isSitePhotoUploaded == 0) {
                        onClick(item, "uploadSitePhoto")

                    }
                }

                if (item.createdAt != null) {
                    dateTv.text = Utils.formatReadableDate(item.createdAt, SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault()))
                }

                items.text = "${item.quantity} items, Total"
                val price = formatPrice(price = item.price?.toDouble() ?: 0.0, currencyType = "INR")
                totalPrice.text = price


            *//*    Utils.setupExpandableTextView(
                    descriptionTv, viewMore = viewMore, item.description ?: "", initialMaxLines = 1
                )*//*

                root.setOnAnimatedClickListener {
                    onClick(item, "product")
                }

               *//* if (item.orderStatus == 0) {
                    statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_pink200))
                    status.text = "Pending"
                    status.setTextColor(ContextCompat.getColor(root.context, R.color.pink))
                } else if (item.orderStatus == 2) {
                    statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_blue))
                    status.text = "Dispatched"
                    status.setTextColor(ContextCompat.getColor(root.context, R.color.blue))
                } else if (item.orderStatus == 3) {
                    statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_green))
                    status.text = "Delivered"
                    status.setTextColor(ContextCompat.getColor(root.context, R.color.green))
                } else if (item.orderStatus == 1) {
                    statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.Light_Deep_Saffron))
                    status.setTextColor(ContextCompat.getColor(root.context, R.color.Deep_Saffron))
                    status.text = "Processed"
                } else if (item.orderStatus == 7 || item.orderStatus == 5) {
                    statusLayout.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.light_pink200))
                    status.text = "Cancelled"
                    status.setTextColor(ContextCompat.getColor(root.context, R.color.pink))
                }*//*
            }*/

        }
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MyOrderItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}