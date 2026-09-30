import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.bharatphoton.databinding.LayoutDropDownItemBinding
import com.app.bharatphoton.databinding.LayoutDropDownListBinding

data class SpinnerDataModel(val id: Any?, val title: Any?)

class SpinnerAdapter(private val onClickItem: (SpinnerDataModel) -> Unit) : ListAdapter<SpinnerDataModel, SpinnerAdapter.ViewHolder>(AsyncDifferConfig.Builder(DiffUtilCallback).build()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.setDataIntoViews(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutDropDownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class ViewHolder(private val binding: LayoutDropDownItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setDataIntoViews(data: SpinnerDataModel) {
            binding.bind(data)
        }

        @SuppressLint("SetTextI18n")
        fun LayoutDropDownItemBinding.bind(data: SpinnerDataModel) {
            TextViewItem.apply {
                text = data.title.toString()
                hint = data.id.toString()
            }

            root.setOnClickListener {
                onClickItem.invoke(data)
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<SpinnerDataModel>() {

        override fun areItemsTheSame(oldItem: SpinnerDataModel, newItem: SpinnerDataModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SpinnerDataModel, newItem: SpinnerDataModel): Boolean {
            return oldItem == newItem
        }
    }

}


@Suppress("DEPRECATION")
fun TextView.dropDown(list: List<SpinnerDataModel>, isShowIndexValue: Int = -1, onClickItem: (SpinnerDataModel) -> Unit) {
    if (isShowIndexValue in list.indices) {
        text = list[isShowIndexValue].title.toString()
    }

    setOnClickListener {
        val binding = LayoutDropDownListBinding.inflate(LayoutInflater.from(context))
        val popupWindow = PopupWindow(binding.root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true).apply {
            elevation = 10f
            isFocusable = true
            isOutsideTouchable = true
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        }


        val location = IntArray(2)
        getLocationOnScreen(location)
        val xOffset = location[0]
        val yOffset = location[1] + height

        if (!(isShowIndexValue > -1 && list.size < 2)) {
            post {
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, xOffset, yOffset)
            }
        }

        val spinnerAdapter by lazy {
            SpinnerAdapter(onClickItem = {
                onClickItem.invoke(it)
                popupWindow.dismiss()
            })
        }
        binding.listView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = spinnerAdapter
        }

        spinnerAdapter.submitList(list)
    }
}