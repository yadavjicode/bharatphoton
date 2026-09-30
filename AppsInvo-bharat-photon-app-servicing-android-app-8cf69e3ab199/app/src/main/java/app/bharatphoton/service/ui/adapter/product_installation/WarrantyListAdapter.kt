package app.bharatphoton.service.ui.adapter.product_installation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.WarrantyListResponseData
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation

class WarrantyListAdapter(
    val context: Context, private val arrResponseData: MutableList<WarrantyListResponseData>, private val preselectedMode: String?,
    private val onItemSelected: (String) -> Unit) : RecyclerView.Adapter<WarrantyListAdapter.ViewHolder>(){

    private var selectedPosition: Int = arrResponseData.indexOfFirst {
        it.warrentyMonth.equals(preselectedMode, ignoreCase = true)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWarranty: TextView = itemView.findViewById(R.id.tvWarranty)
        val ivSelect: ImageView = itemView.findViewById(R.id.ivSelect)
        val llItem: LinearLayout = itemView.findViewById(R.id.llItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_warranty_list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = arrResponseData[holder.adapterPosition]

        // Set the text based on the mode
        when (currentItem.warrentyMonth) {
            "H" -> {
                holder.tvWarranty.text = "Half Yearly"
            }
            "Y" -> {
                holder.tvWarranty.text = "Yearly"
            }
            "T" -> {
                holder.tvWarranty.text = "2 Years"
            }
            "F" -> {
                holder.tvWarranty.text = "5 Years"
            }
            /*else -> {
                holder.tvWarranty.text = "Yearly"
            }*/
        }

        if (holder.adapterPosition == selectedPosition) {
            holder.tvWarranty.setTextColor(ContextCompat.getColor(context, R.color.photon_orange))
            holder.ivSelect.setImageResource(R.drawable.radio_selected)
            holder.llItem.setBackgroundResource(R.drawable.orange_10stroked_bg)
        } else {
            holder.tvWarranty.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.ivSelect.setImageResource(R.drawable.radio_unselected)
            holder.llItem.setBackgroundResource(R.drawable.black_10stroked_bg)
        }

        holder.itemView.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                currentItem.warrentyMonth?.let { it1 -> onItemSelected(it1) }
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }

}
