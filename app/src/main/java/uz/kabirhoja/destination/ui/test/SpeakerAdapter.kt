package uz.kabirhoja.destination.ui.test

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kabirhoja.destination.R
import uz.kabirhoja.destination.data.model.SpeakerItem
import com.kabirhoja.destination.databinding.RecycleSpeakerItemLayoutBinding
import uz.kabirhoja.destination.ui.common.AnimationButton.animateClick

class SpeakerAdapter : RecyclerView.Adapter<SpeakerAdapter.SpeakerViewHolder>() {
    private var listener: OnClickItemListener? = null
    private val list: MutableList<SpeakerItem> = ArrayList()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class SpeakerViewHolder(private val binding: RecycleSpeakerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: SpeakerItem) {
            when {
                item.locale.contains("US", ignoreCase = true) -> {
                    binding.languageIcon.setImageResource(R.drawable.ic_flag_us)
                    binding.languageName.text = "American"
                }

                item.locale.contains("GB", ignoreCase = true) || item.locale.contains("UK", ignoreCase = true) -> {
                    binding.languageIcon.setImageResource(R.drawable.ic_flag_uk)
                    binding.languageName.text = "British"
                }

                item.locale.contains("AU", ignoreCase = true) -> {
                    binding.languageIcon.setImageResource(R.drawable.ic_au)
                    binding.languageName.text = "Australian"
                }

                else -> {
                    binding.languageIcon.setImageResource(R.drawable.ic_flag_us)
                    binding.languageName.text = "English"
                }
            }

            binding.languageNumber.text = "Voice №" + (item.number + 1).toString()

            if (adapterPosition == selectedPosition) {
                updateBackgrounds(binding.languageName)
            } else {
                resetBackgrounds(binding.languageName)
            }

            binding.root.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition
                if (previousSelectedPosition != RecyclerView.NO_POSITION && previousSelectedPosition < list.size) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(selectedPosition)
                listener?.onClickItem(item)
                it.animateClick()
            }
        }
    }

    private fun updateBackgrounds(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green))
    }

    private fun resetBackgrounds(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val binding = RecycleSpeakerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpeakerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun setOnClickListener(onClickItemListener: OnClickItemListener) {
        listener = onClickItemListener
    }

    interface OnClickItemListener {
        fun onClickItem(item: SpeakerItem)
    }

    fun updateList(newList: List<SpeakerItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun setSelectedSpeakerName(speakerName: String) {
        val index = list.indexOfFirst { it.name == speakerName }
        if (index != -1) {
            val previous = selectedPosition
            selectedPosition = index
            if (previous != RecyclerView.NO_POSITION && previous < list.size) {
                notifyItemChanged(previous)
            }
            notifyItemChanged(selectedPosition)
        }
    }
}
