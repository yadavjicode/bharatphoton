package app.bharatphoton.service.ui.adapter.product_subscritption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.ProductDetailInstallationDetailsResponse
import app.bharatphoton.service.databinding.RemarksLayoutItemViewBinding
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionInstallationDetailsRemarksAdapter.ViewHolder

class SubscriptionInstallationDetailsRemarksAdapter : ListAdapter<ProductDetailInstallationDetailsResponse, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: RemarksLayoutItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductDetailInstallationDetailsResponse) {
            with(binding) {
                finTv.text = data.finNo
                nameTv.text = data.customerName
                remarkTv.text = data.remark
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RemarksLayoutItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ProductDetailInstallationDetailsResponse>() {
        override fun areItemsTheSame(oldItem: ProductDetailInstallationDetailsResponse, newItem: ProductDetailInstallationDetailsResponse): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: ProductDetailInstallationDetailsResponse, newItem: ProductDetailInstallationDetailsResponse): Boolean {
            return oldItem == newItem
        }
    }
}