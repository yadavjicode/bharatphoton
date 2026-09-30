package app.bharatphoton.service.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.R

class ReviewAndRatingAdapter(val context: Context, private val arrModel: ArrayList<ReviewModel>)
    : RecyclerView.Adapter<ReviewAndRatingAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_review_rating_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return arrModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = arrModel[position].name
        holder.tvTime.text = arrModel[position].time
    }
}

data class ReviewModel(
    var name: String,
    var time: String
)