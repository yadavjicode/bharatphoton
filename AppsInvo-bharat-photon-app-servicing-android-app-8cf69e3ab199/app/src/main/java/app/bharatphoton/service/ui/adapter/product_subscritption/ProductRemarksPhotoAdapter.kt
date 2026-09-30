package app.bharatphoton.service.ui.adapter.product_subscritption

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ProductDetailResponse.ProductDetailResponseData.ProductDetailResponseInstallationDetails
import app.bharatphoton.service.databinding.RecyclerInstallationPhotosLayoutBinding
import app.bharatphoton.service.ui.adapter.product_subscritption.ProductRemarksPhotoAdapter.ViewHolder
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class ProductRemarksPhotoAdapter(val onClick: (String, ProductDetailResponseInstallationDetails, Int) -> Unit) : ListAdapter<ProductDetailResponseInstallationDetails, ViewHolder>(DiffUtil) {
    inner class ViewHolder(private val binding: RecyclerInstallationPhotosLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductDetailResponseInstallationDetails) {
            with(binding) {
                // Ensure the image list is not null and contains at least one element
                val fileUrl = if (!data.image.isNullOrEmpty() && data.image.size > 0) {
                    data.image[0].url ?: ""
                } else {
                    ""
                }

                // Load image only if fileUrl is valid
                if (fileUrl.isNotEmpty()) {
                    Glide.with(root.context).load(fileUrl).placeholder(R.drawable.default_gray_image_img).error(R.drawable.default_gray_image_img).into(ivInstalledImg)

                    itemView.setOnClickWithAnimation {
                        if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                        onClick("root", data, layoutPosition)
                    }

                    // Handle video play button visibility
                    val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)
                    ivPlay.visibility = if (isVideo(fileUrl)) View.VISIBLE else View.GONE
                } else {
                    ivInstalledImg.setImageResource(R.drawable.default_gray_image_img)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerInstallationPhotosLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun isVideo(url: String): Boolean {
        return url.endsWith(".mp4", true) || url.endsWith(".mov", true) || url.endsWith(".avi", true) || url.endsWith(".mkv", true) || url.endsWith(".flv", true) || url.endsWith(".wmv", true)
    }

    private object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<ProductDetailResponseInstallationDetails>() {
        override fun areItemsTheSame(oldItem: ProductDetailResponseInstallationDetails, newItem: ProductDetailResponseInstallationDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductDetailResponseInstallationDetails, newItem: ProductDetailResponseInstallationDetails): Boolean {
            return oldItem == newItem
        }
    }
}