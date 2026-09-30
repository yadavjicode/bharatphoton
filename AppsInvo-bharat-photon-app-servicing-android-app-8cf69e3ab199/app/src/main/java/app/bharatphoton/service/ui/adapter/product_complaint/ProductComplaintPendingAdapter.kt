package app.bharatphoton.service.ui.adapter.product_complaint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ProductComplaintListingResponse.ProductComplaintListingResponseData
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.ui.activities.product_complaint.ComplaintDetailsActivity
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COMPLAINT_STATUS
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class ProductComplaintPendingAdapter(val context: Context, val arrResponseData: MutableList<ProductComplaintListingResponseData>)
    : RecyclerView.Adapter<ProductComplaintPendingAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        val tvTicketNumber: TextView = itemView.findViewById(R.id.tvTicketNumber)
        val tvComplaintIssue: TextView = itemView.findViewById(R.id.tvComplaintIssue)
        val tvComplaintDescription: TextView = itemView.findViewById(R.id.tvComplaintDescription)
        val tvResolveDate: TextView = itemView.findViewById(R.id.tvResolveDate)
        val tvBtnView: TextView = itemView.findViewById(R.id.tvBtnView)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_product_complaint_pending_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(arrResponseData[position]?.profileImage)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(holder.ivProfile)
        if (!arrResponseData[position]?.companyName.isNullOrEmpty()){
            holder.tvName.text = arrResponseData[position]?.companyName
        }else {
            if (!arrResponseData[position]?.firstName.isNullOrEmpty() && !arrResponseData[position]?.lastName.isNullOrEmpty()) {
                holder.tvName.text = arrResponseData[position]?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + arrResponseData[position]?.lastName?.replaceFirstChar { it.uppercaseChar() } //products?.map { it?.product_name.toString() }.toString()
            } else {
                holder.tvName.text = "NA"
            }
        }
        holder.tvTicketNumber.text = "#" + arrResponseData[position].uuid.toString()
        holder.tvComplaintIssue.text = arrResponseData[position].complaintReason.toString()
        if (!arrResponseData[position].description.isNullOrEmpty()) {
            holder.tvComplaintDescription.isVisible = true
            holder.tvComplaintDescription.text = arrResponseData[position].description ?: ""
        }else{
            holder.tvComplaintDescription.isVisible = false
        }
        holder.tvResolveDate.text = (context as BaseActivity).formatDateInStringFormat(arrResponseData[position].resolveDate.toString())
        if (arrResponseData[position]?.phoneNo != null) {
            holder.tvPhone.text = arrResponseData[position]?.countryCode.toString()+" "+arrResponseData[position]?.phoneNo.toString()
        }else{
            holder.tvPhone.text = "NA"
        }

        holder.tvBtnView.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent= Intent(context, ComplaintDetailsActivity::class.java)
            intent.putExtra(COMPLAINT_ID,arrResponseData[position].id)
            intent.putExtra(COMPLAINT_STATUS,"1")
            intent.putExtra(FROM_ADAPTER,"ProductComplaintPendingAdapter")
            context.startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(newData: List<ProductComplaintListingResponseData>?) {
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