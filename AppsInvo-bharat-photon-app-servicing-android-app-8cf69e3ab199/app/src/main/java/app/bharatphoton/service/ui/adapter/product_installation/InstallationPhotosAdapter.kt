package app.bharatphoton.service.ui.adapter.product_installation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ProductDetailResponse
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class InstallationPhotosAdapter(
    val context: Context, private val arrLanguage: MutableList<ProductDetailResponse.ProductDetailResponseData.ProductDetailResponseAddressDetails.ProductDetailResponseAddressDetailsImage>,
    private val listener: OnItemClickListener)
    : RecyclerView.Adapter<InstallationPhotosAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onInstallationMediaClick(position: Int, url: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivInstalledImg: ImageView = itemView.findViewById(R.id.ivInstalledImg)
        init {
            itemView.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onInstallationMediaClick(position, arrLanguage[position].url.toString())
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_installation_photos_layout, parent, false))
    }
    override fun getItemCount(): Int {
        return arrLanguage.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(arrLanguage[position].url)
            .placeholder(R.drawable.default_gray_image_img)
            .error(R.drawable.default_gray_image_img)
            .into(holder.ivInstalledImg)
    }
}