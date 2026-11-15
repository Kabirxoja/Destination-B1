package uz.kabirhoja.destination.ui.search

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kabirhoja.destination.R
import uz.kabirhoja.destination.data.model.Vocabulary
import com.kabirhoja.destination.databinding.FragmentSearchBinding
import uz.kabirhoja.destination.ui.common.MainViewModelFactory
import uz.kabirhoja.destination.data.local.MainSharedPreference
import uz.kabirhoja.destination.ui.common.setupSmoothScroll
import java.util.Locale

class SearchFragment : Fragment(), SearchAdapter.OnNoteClickListener, TextToSpeech.OnInitListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var tts: TextToSpeech


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireActivity().application)
        )[SearchViewModel::class.java]

        searchAdapter = SearchAdapter()
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.parentRecyclerView.setupSmoothScroll()
        searchViewModel.searchItems("")


        searchViewModel.filteredWords.observe(viewLifecycleOwner) { words ->
            binding.parentRecyclerView.adapter = searchAdapter
            searchAdapter.submitList(words.sortedBy { it.englishWord })
            searchAdapter.setOnNoteClickListener(this)
        }


        setupCancelIconVisibility()
        setupCancelClickListener()
        setTextSpeech()
    }

    private fun setTextSpeech(){
        tts = TextToSpeech(binding.root.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
            } else {
                Toast.makeText(binding.root.context, "Speech voice initialization failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupCancelIconVisibility() {
        val cancelIcon: Drawable? =
            AppCompatResources.getDrawable(binding.root.context, R.drawable.ic_cancel)

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                } else {
                    binding.searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        cancelIcon,
                        null
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
                searchViewModel.searchItems(s.toString())
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCancelClickListener() {
        binding.searchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawables = binding.searchEditText.compoundDrawablesRelative
                val cancelIcon = drawables[2]
                if (cancelIcon != null) {
                    val touchAreaStart = binding.searchEditText.width - binding.searchEditText.paddingEnd - cancelIcon.bounds.width() - 30
                    if (event.x >= touchAreaStart) {
                        binding.searchEditText.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAudioClick(vocabularyEntity: Vocabulary) {
        speakWord(
            vocabularyEntity.englishWord.replace("sb", "somebody")
                .replace("sth", "something")
                .split(Regex("\\b(adj|v|adv|n)\\b"))
                .firstOrNull() ?: ""
        )
    }

    private fun speakWord(word: String) {
        val speakerType = MainSharedPreference.getSpeakerType(binding.root.context)
        if (speakerType.isEmpty()) {
            Toast.makeText(binding.root.context, "Speaker type not selected", Toast.LENGTH_SHORT).show()
        } else {
            speakWordWithType(word, speakerType)
        }
    }

    private fun speakWordWithType(word: String, voiceType: String) {
        if (tts == null) {
            println("TTS is not initialized.")
            return
        }

        val availableVoices = tts.voices

        val voice = availableVoices.find { it.name == voiceType }

        if (voice != null) {
            tts.voice = voice
            tts.setSpeechRate(0.9f) // Adjust speech rate
            tts.setPitch(1.1f)      // Adjust pitch
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            println("Voice $voiceType not found. Available voices: ${availableVoices.map { it.name }}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS)
            tts.language = Locale.ENGLISH
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }

}
