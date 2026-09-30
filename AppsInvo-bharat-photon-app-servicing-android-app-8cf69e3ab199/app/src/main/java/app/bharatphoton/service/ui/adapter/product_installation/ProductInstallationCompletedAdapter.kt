package app.bharatphoton.service.ui.adapter.product_installation

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
import app.bharatphoton.service.api_model_classes.ProductInstallationList
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.ui.activities.product_installation.ProductDetailsActivity
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.INSTALLATION_STATUS
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide

class ProductInstallationCompletedAdapter(val context: Context, private val arrResponseData: MutableList<ProductInstallationList.ProductInstallationListData>) : RecyclerView.Adapter<ProductInstallationCompletedAdapter.ViewHolder>(){

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
        val userDetails = arrResponseData[position].userDetails
        Glide.with(context)
            .load(arrResponseData[position].userDetails?.profileImage)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(holder.ivProfile)
        if (!userDetails?.companyName.isNullOrEmpty()){
            holder.tvName.text = userDetails?.companyName
        }else{
            if (!arrResponseData[position].userDetails?.firstName.isNullOrEmpty() && !arrResponseData[position].userDetails?.lastName.isNullOrEmpty()){
                holder.tvName.text = arrResponseData[position].userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + arrResponseData[position].userDetails?.lastName?.replaceFirstChar { it.uppercaseChar() } //products?.map { it?.product_name.toString() }.toString()
            }else{
                holder.tvName.text = "NA"
            }
        }
        holder.tvTicketNo.text = "#" + arrResponseData[position].uuid.toString()
        if (arrResponseData[position].product != null){
            holder.tvResolveDate.text = (context as BaseActivity).formatDateInStringFormat(arrResponseData[position].product?.resolveDate.toString())
        }

        holder.itemView.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val productId = arrResponseData[position].product
            if (productId != null) {
                val intent= Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(PRODUCT_ID,productId.id)
                intent.putExtra(FROM_ADAPTER,"ProductInstallationCompletedAdapter")
                intent.putExtra(INSTALLATION_STATUS,"2")
                context.startActivity(intent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(newData: List<ProductInstallationList.ProductInstallationListData>?) {
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