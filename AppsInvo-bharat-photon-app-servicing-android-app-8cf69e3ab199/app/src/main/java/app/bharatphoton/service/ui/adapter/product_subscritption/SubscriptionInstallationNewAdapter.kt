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

class SubscriptionInstallationNewAdapter(val context: Context, private val arrResponseData: MutableList<SubscriptionInstallationListResponseData>) : RecyclerView.Adapter<SubscriptionInstallationNewAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTicketNo: TextView = itemView.findViewById(R.id.tvTicketNo)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvResolveDate: TextView = itemView.findViewById(R.id.tvResolveDate)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_product_installation_new_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userDetails = arrResponseData[position]
        Glide.with(context)
            .load(userDetails?.profileImage)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(holder.ivProfile)

        if (!userDetails?.companyName.isNullOrEmpty()){
            holder.tvName.text = userDetails?.companyName
        }else{
            if (!userDetails?.firstName.isNullOrEmpty() && !userDetails?.lastName.isNullOrEmpty()){
                holder.tvName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + userDetails?.lastName?.replaceFirstChar { it.uppercaseChar() } //products?.map { it?.product_name.toString() }.toString()
            }else{
                holder.tvName.text = "NA"
            }
        }

        holder.tvTicketNo.text = "#" + userDetails?.uuid.toString()
        if (userDetails?.resolveDate != null){
            holder.tvResolveDate.text = (context as BaseActivity).formatDateInStringFormat(userDetails?.resolveDate.toString())
        }
        if (!userDetails?.businessPhoneNo.isNullOrEmpty()){
            holder.tvPhone.text = userDetails?.country_code.toString() + " " + userDetails?.businessPhoneNo.toString()
        }else {
            if (userDetails?.phoneNo != null) {
                holder.tvPhone.text = userDetails?.country_code.toString() + " " + userDetails?.phoneNo.toString()
            } else {
                holder.tvPhone.text = "NA"
            }
        }

        holder.itemView.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val productId = arrResponseData[position]._id
            if (productId != null) {
                val intent= Intent(context, SubscriptionDetailsActivity::class.java)
                intent.putExtra(PRODUCT_ID, productId)
                intent.putExtra(INSTALLATION_STATUS,"0")
                intent.putExtra(FROM_ADAPTER,"SubscriptionInstallationNewAdapter")
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
