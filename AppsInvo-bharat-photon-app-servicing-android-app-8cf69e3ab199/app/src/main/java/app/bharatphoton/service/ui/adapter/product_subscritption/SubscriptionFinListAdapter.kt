package app.bharatphoton.service.ui.adapter.product_subscritption

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ProductDetailInstallationDetailsResponse
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation

class SubscriptionFinListAdapter(
    val context: Context, private val arrResponseData: MutableList<ProductDetailInstallationDetailsResponse>?, private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<SubscriptionFinListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFin: TextView = itemView.findViewById(R.id.tvFin)
        val llItem: LinearLayout = itemView.findViewById(R.id.llItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_fin_list_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrResponseData?.size ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val installationData = arrResponseData?.get(position)

        if (installationData != null) {
            val finNo = installationData.finNo

            if (finNo != null) {
                if (finNo.isNotEmpty()) {
                    holder.tvFin.text = finNo
                } else {
                    holder.tvFin.text = "NA"
                }
            }
        }
        when (arrResponseData?.get(position)?.installationStatus) {
            0 -> {
                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    if (installationData != null) {
                        val sinUpdateId = installationData._id
                        val finNo = installationData.finNo
                        if (sinUpdateId != null) {
                            if (finNo != null) {
                                onItemClick(finNo, sinUpdateId)
                            }
                        }
                    }
                }
            }

            1 -> {
                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    if (installationData != null) {
                        val sinUpdateId = installationData._id
                        val finNo = installationData.finNo
                        if (sinUpdateId != null) {
                            if (finNo != null) {
                                onItemClick(finNo, sinUpdateId)
                            }
                        }
                    }
                }
            }

            2 -> {/* holder.llItem.isClickable = false
                holder.tvFin.apply {
                    setBackgroundResource(R.drawable.gray_5cornered_stroked_bg)
                    setTextColor(ContextCompat.getColor(context, R.color.photon_dark_gray))
                }*/

                holder.llItem.setOnClickWithAnimation {
                    if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                    if (installationData != null) {
                        val sinUpdateId = installationData._id ?: ""
                        val finNo = installationData.finNo ?: ""
                        onItemClick(finNo, sinUpdateId)
                    }
                }
            }
        }
    }

}
