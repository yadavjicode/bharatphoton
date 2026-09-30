package com.app.bharatphoton.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.MyAddressListAdapter.ViewHolder
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.MyAddressListItemViewBinding
import com.app.bharatphoton.module.addressResponse.AddressListResponse.AddressListData

class MyAddressListAdapter(val onClick: (AddressListData, View?, String) -> Unit, var addressId: String = "") : ListAdapter<AddressListData, ViewHolder>(DiffUtil) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MyAddressListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class ViewHolder(private val binding: MyAddressListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: AddressListData) {
            with(binding) {
                locationName.text = "${data.state} (${data.city})"
                address.text = "${data.address} \n${data.city}, ${data.state}, ${data.zipCode} \n\n${data.countryCode} ${data.phoneNo}"

                menu.setOnClickListener {
                    onClick(data, it, "more")
                }

                if (data.id == addressId) {
                    data.selectedAddress = "1"
                }

                if (data.selectedAddress == "0") {
                    radioImg.setImageResource(R.drawable.radio_btn_unselect)

                } else {
                    radioImg.setImageResource(R.drawable.radio_selected)

                }

                root.setOnAnimatedClickListener {
                    currentList.forEach {
                        it.selectedAddress = "0"
                    }
                    data.selectedAddress = "1"
                    notifyDataSetChanged()
                    onClick(data, null, "root")
                }
            }
        }
    }

    private object DiffUtil : ItemCallback<AddressListData>() {
        override fun areItemsTheSame(oldItem: AddressListData, newItem: AddressListData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AddressListData, newItem: AddressListData): Boolean {
            return oldItem == newItem
        }
    }
}