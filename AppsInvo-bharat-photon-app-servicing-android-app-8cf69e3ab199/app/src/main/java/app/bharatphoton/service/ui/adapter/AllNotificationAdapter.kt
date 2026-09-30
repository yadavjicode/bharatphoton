package app.bharatphoton.service.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.NotificationResponseData
import app.bharatphoton.service.base.BaseActivity
import com.bumptech.glide.Glide

class AllNotificationAdapter(val context: Context, private val arrModel: MutableList<NotificationResponseData>) : RecyclerView.Adapter<AllNotificationAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvNotification: TextView = itemView.findViewById(R.id.tvNotification)
        val tvNotificationBody: TextView = itemView.findViewById(R.id.tvNotificationBody)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_all_notification_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(arrModel[position].profileImage)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(holder.ivProfile)
        holder.tvNotification.text = arrModel[position].title
        holder.tvNotificationBody.text = arrModel[position].body
        holder.tvDate.text = arrModel[position].createdAt?.let { (context as BaseActivity).formatDateInStringFormat(it) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(newData: List<NotificationResponseData>?) {
        newData?.let {
            arrModel.addAll(it)
            notifyDataSetChanged()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun clearAll() {
        arrModel.clear()
        notifyDataSetChanged()
    }
}