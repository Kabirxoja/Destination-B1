package uz.kabirhoja.destination.ui.adapters

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.kabirhoja.destination.data.data.Vocabulary
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kabirhoja.destination.R
import uz.kabirhoja.destination.custom.AnimationButton.animateClick

class VocabularyAdapter : ListAdapter<Vocabulary, VocabularyAdapter.ParentViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_main_item_layout, parent, false)
        return ParentViewHolder(view, parent as RecyclerView)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parentItem = getItem(position)
        holder.bind(parentItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<Vocabulary>() {
        override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem.englishWord == newItem.englishWord
        }

        override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
            return oldItem == newItem // Or implement a more specific check if needed
        }
    }

    inner class ParentViewHolder(itemView: View, private val recyclerView: RecyclerView) :
        RecyclerView.ViewHolder(itemView) {

        private val enTextView: TextView = itemView.findViewById(R.id.en_text_view)
        private val uzTextView: TextView = itemView.findViewById(R.id.uz_text_view)
        private val audioSpeaker: ImageView = itemView.findViewById(R.id.audio_speaker)
        private val addNote: ImageView = itemView.findViewById(R.id.add_note)
        private val selectionType: TextView = itemView.findViewById(R.id.selection_type)


        fun bind(parentItem: Vocabulary) {
            uzTextView.text = parentItem.translatedWord
            enTextView.text = parentItem.englishWord
            selectionType.text = parentItem.type
            correctionText(parentItem.type!!)
            if (parentItem.isNoted == 1) addNote.setImageResource(R.drawable.ic_note_true)
            else addNote.setImageResource(R.drawable.ic_note_false)
        }

        private fun showBottomSheet(parentItem: Vocabulary, itemView: View) {
            val bottomSheetDialog = BottomSheetDialog(itemView.context)
            val bottomSheetView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.fragment_bottom_sheet_vocabulary, null)

            bottomSheetView.findViewById<TextView>(R.id.en_word_bottom_sheet).text =
                parentItem.exampleInEnglish
            bottomSheetView.findViewById<TextView>(R.id.uz_word_bottom_sheet).text =
                parentItem.exampleTranslatedWord
            bottomSheetView.findViewById<TextView>(R.id.defination_bottom_sheet).text =
                parentItem.definition

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }


        init {
            itemView.rootView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    (itemView.context as? AppCompatActivity)?.let {
                        val adapter = (recyclerView.adapter as? VocabularyAdapter)
                            ?: return@setOnClickListener
                        val parentItem = adapter.getItem(position)
                        showBottomSheet(parentItem, itemView)
                        adapter.notifyItemChanged(position)
                    }

                }
                it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
            }
            audioSpeaker.setOnClickListener {
                audioSpeaker.setImageResource(R.drawable.ic_audio_on) // Change icon when clicked
                Handler(Looper.getMainLooper()).postDelayed({
                    audioSpeaker.setImageResource(R.drawable.ic_audio_off) // Reset only this button
                }, 2000)

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    noteClickListener?.onAudioClick(item)
                }
                it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.86f)
            }

            addNote.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    noteClickListener?.onNoteClick(item)
                }
                it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.86f)
            }
        }

        private fun correctionText(string: String) {
            when (string) {
                "topic_vocabulary" -> selectionType.text = "vocabulary"
                "phrasal_verbs" -> selectionType.text = "phrasal verbs"
                "prepositional_phrases" -> selectionType.text = "prepositional phrases"
                "word_formation" -> selectionType.text = "word formation"
                "word_patterns" -> selectionType.text = "word patterns"
            }
        }
    }

    interface OnNoteClickListener {
        fun onNoteClick(vocabularyEntity: Vocabulary)
        fun onAudioClick(vocabularyEntity: Vocabulary)
    }

    private var noteClickListener: OnNoteClickListener? = null

    fun setOnNoteClickListener(listener: OnNoteClickListener) {
        noteClickListener = listener
    }


}