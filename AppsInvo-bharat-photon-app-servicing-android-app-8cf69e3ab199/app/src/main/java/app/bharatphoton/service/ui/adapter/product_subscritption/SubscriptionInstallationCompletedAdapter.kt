package app.bharatphoton.service.ui.adapter.product_subscritption

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.SubscriptionInstallationListResponseData
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.ui.activities.product_subscription.SubscriptionDetailsActivity
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.INSTALLATION_STATUS
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class SubscriptionInstallationCompletedAdapter(val context: Context, private val arrResponseData: MutableList<SubscriptionInstallationListResponseData>) : RecyclerView.Adapter<SubscriptionInstallationCompletedAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTicketNo: TextView = itemView.findViewById(R.id.tvTicketNo)
        val tvResolveDate: TextView = itemView.findViewById(R.id.tvResolveDate)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_product_installation_completed_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userDetails = arrResponseData[position]
        Glide.with(context)
            .load(arrResponseData[position].profileImage)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(holder.ivProfile)
        if (!userDetails?.companyName.isNullOrEmpty()){
            holder.tvName.text = userDetails?.companyName
        }else{
            if (!arrResponseData[position].firstName.isNullOrEmpty() && !arrResponseData[position].lastName.isNullOrEmpty()){
                holder.tvName.text = arrResponseData[position].firstName?.replaceFirstChar { it.uppercaseChar() } + " " + arrResponseData[position].lastName?.replaceFirstChar { it.uppercaseChar() } //products?.map { it?.product_name.toString() }.toString()
            }else{
                holder.tvName.text = "NA"
            }
        }
        holder.tvTicketNo.text = "#" + arrResponseData[position].uuid.toString()
        if (arrResponseData[position].resolveDate != null){
            holder.tvResolveDate.text = (context as BaseActivity).formatDateInStringFormat(arrResponseData[position].resolveDate.toString())
        }

        holder.itemView.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val productId = arrResponseData[position]._id
            if (productId != null) {
                val intent= Intent(context, SubscriptionDetailsActivity::class.java)
                intent.putExtra(PRODUCT_ID,productId)
                intent.putExtra(FROM_ADAPTER,"SubscriptionInstallationCompletedAdapter")
                intent.putExtra(INSTALLATION_STATUS,"2")
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(newData: List<SubscriptionInstallationListResponseData>?) {
        newData?.let {
            arrResponseData.addAll(it)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAll() {
        arrResponseData.clear()
        notifyDataSetChanged()
    }
}