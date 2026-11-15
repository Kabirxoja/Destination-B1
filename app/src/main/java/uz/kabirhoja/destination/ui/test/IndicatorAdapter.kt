package uz.kabirhoja.destination.ui.test

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.kabirhoja.destination.R
import com.kabirhoja.destination.databinding.RecycleIndicatorItemBinding
import uz.kabirhoja.destination.ui.common.AnimationButton.animateClick
import uz.kabirhoja.destination.data.model.TestChoiceItem

class IndicatorAdapter : RecyclerView.Adapter<IndicatorAdapter.MyViewHolder>() {

    private var list: MutableList<TestChoiceItem> = ArrayList()
    private var listener: OnClickItemListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = RecycleIndicatorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: RecycleIndicatorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < list.size) {
                    val currentItem = list[position]
                    currentItem.checked = !currentItem.checked
                    notifyItemChanged(position)
                    listener?.onClickItem(currentItem)
                    Log.d("llll", currentItem.toString())
                    it.animateClick()
                }
            }
        }

        fun onBind(item: TestChoiceItem) {
            binding.unitText.text = "Unit"
            binding.numberText.text = item.unitNumber.toString()

            val isChecked = item.checked
            val backgroundColor = if (isChecked) R.color.green else R.color.white
            val textColor = if (isChecked) R.color.white else R.color.black
            val color = ContextCompat.getColor(binding.root.context, backgroundColor)
            val colorStateList = ColorStateList.valueOf(color)

            ViewCompat.setBackgroundTintList(
                binding.indicatorRecyclerView,
                colorStateList
            )
            binding.unitText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    textColor
                )
            )
            binding.numberText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    textColor
                )
            )
        }
    }

    fun setOnClickListener(onClickItemListener: OnClickItemListener) {
        listener = onClickItemListener
    }

    interface OnClickItemListener {
        fun onClickItem(item: TestChoiceItem)
    }

    fun updateList(newList: List<TestChoiceItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
