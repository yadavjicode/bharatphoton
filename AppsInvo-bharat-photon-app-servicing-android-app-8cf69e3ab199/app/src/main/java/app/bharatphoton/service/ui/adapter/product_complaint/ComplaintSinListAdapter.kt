package app.bharatphoton.service.ui.adapter.product_complaint

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponseComplaintDetails
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation

class ComplaintSinListAdapter(
    val context: Context, private val arrResponseData: MutableList<ComplaintDetailsResponseComplaintDetails>,
    private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<ComplaintSinListAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFin: TextView = itemView.findViewById(R.id.tvFin)
        val llItem: LinearLayout = itemView.findViewById(R.id.llItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_fin_list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sinNo = arrResponseData[position].sin ?: ""
        if (sinNo.isNotEmpty()){
            holder.tvFin.text = sinNo
        }else{
            holder.tvFin.text = "NA"
        }
        when(arrResponseData[position].complaintStatus){
            0 -> {
                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    onItemClick(sinNo)
                }
            }
            1 -> {
                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    onItemClick(sinNo)
                }
            }
            2 -> {
              /*  holder.llItem.isClickable = false
                holder.tvFin.apply {
                    setBackgroundResource(R.drawable.gray_5cornered_stroked_bg)
                    setTextColor(ContextCompat.getColor(context, R.color.photon_dark_gray))
                }*/
                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    onItemClick(sinNo)
                }
            }
        }

    }

}
