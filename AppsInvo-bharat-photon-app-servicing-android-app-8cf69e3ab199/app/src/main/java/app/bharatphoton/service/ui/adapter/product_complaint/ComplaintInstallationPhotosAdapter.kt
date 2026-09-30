package app.bharatphoton.service.ui.adapter.product_complaint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponseImage
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class ComplaintInstallationPhotosAdapter(
    val context: Context, private val arrModel: MutableList<ComplaintDetailsResponseImage?>, private val listener: OnImageClickListener
) : RecyclerView.Adapter<ComplaintInstallationPhotosAdapter.ViewHolder>() {

    interface OnImageClickListener {
        fun onComplaintInstallationMediaClick(position: Int, complaintUrl: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivInstalledImg: ImageView = itemView.findViewById(R.id.ivInstalledImg)
        val ivPlay: ImageView = itemView.findViewById(R.id.ivPlay)

        init {
            itemView.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                val position = layoutPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onComplaintInstallationMediaClick(position, arrModel[position]?.url.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_installation_photos_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileUrl = arrModel[position]?.url ?: ""
        Glide.with(context).load(fileUrl).placeholder(R.drawable.default_gray_image_img).error(R.drawable.default_gray_image_img).into(holder.ivInstalledImg)

        if (isVideo(fileUrl)) {
            holder.ivPlay.visibility = View.VISIBLE
        } else {
            holder.ivPlay.visibility = View.GONE
        }
    }

    private fun isVideo(url: String): Boolean {
        return url.endsWith(".mp4", true) || url.endsWith(".mov", true) || url.endsWith(".avi", true) || url.endsWith(".mkv", true) || url.endsWith(".flv", true) || url.endsWith(".wmv", true)
    }
}