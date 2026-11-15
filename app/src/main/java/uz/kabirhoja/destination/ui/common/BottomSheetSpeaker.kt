package uz.kabirhoja.destination.ui.common

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kabirhoja.destination.databinding.FragmentBottomSheetSpeakerBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.kabirhoja.destination.data.local.MainSharedPreference
import uz.kabirhoja.destination.data.model.SpeakerItem
import uz.kabirhoja.destination.ui.test.SpeakerAdapter
import java.util.ArrayList
import java.util.Locale

class BottomSheetSpeaker : BottomSheetDialogFragment(), SpeakerAdapter.OnClickItemListener,
    TextToSpeech.OnInitListener {

    private var _binding: FragmentBottomSheetSpeakerBinding? = null
    private val binding get() = _binding!!
    private var listener: BottomSheetSpeakerListener? = null
    private lateinit var adapter: SpeakerAdapter
    private var speakerSelected = ""
    private lateinit var tts: TextToSpeech
    private var list: List<SpeakerItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetSpeakerBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.bottomSingleButton.setOnClickListener {
            if (speakerSelected.isNotEmpty()) {
                listener?.onSelectedSpeaker(speakerSelected)
            }
            dismiss()
        }

        adapter = SpeakerAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(root.context, 3)
        adapter.setOnClickListener(this)

        tts = TextToSpeech(requireContext().applicationContext, this)

        return root
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.ENGLISH
            lifecycleScope.launch {
                loadVoicesWithRetry()
            }
        } else {
            Log.e("TTS", "TTS initialization failed.")
        }
    }

    private suspend fun loadVoicesWithRetry() {
        var voicesList = emptyList<SpeakerItem>()
        for (i in 0..10) {
            voicesList = speakWord()
            if (voicesList.isNotEmpty()) break
            delay(200)
        }

        if (voicesList.isEmpty()) {
            voicesList = speakWordFallback()
        }

        if (voicesList.isNotEmpty()) {
            list = voicesList
            adapter.updateList(list)

            val savedSpeaker = MainSharedPreference.getSpeakerType(requireContext())
            if (savedSpeaker.isNotEmpty()) {
                adapter.setSelectedSpeakerName(savedSpeaker)
                speakerSelected = savedSpeaker
            }
        }
    }

    private fun speakWord(): List<SpeakerItem> {
        val availableVoices = try {
            tts.voices
        } catch (e: Exception) {
            null
        } ?: return emptyList()

        val allowedLocales = listOf("en_US", "en_GB", "en_AU")
        val filteredVoices = availableVoices.filter { voice ->
            val localeStr = voice.locale.toString()
            val isEnglish = voice.locale.language.equals("en", ignoreCase = true)
            isEnglish && (allowedLocales.any { localeStr.equals(it, ignoreCase = true) } ||
                    listOf("US", "GB", "AU").contains(voice.locale.country.uppercase())) &&
                    !voice.isNetworkConnectionRequired
        }

        val resultList = mutableListOf<SpeakerItem>()
        for (i in filteredVoices.indices) {
            resultList.add(
                SpeakerItem(
                    i,
                    filteredVoices[i].name,
                    filteredVoices[i].locale.toString(),
                    filteredVoices[i].quality,
                    filteredVoices[i].latency,
                    filteredVoices[i].isNetworkConnectionRequired
                )
            )
        }
        return resultList
    }

    private fun speakWordFallback(): List<SpeakerItem> {
        val availableVoices = try {
            tts.voices
        } catch (e: Exception) {
            null
        } ?: return emptyList()

        val filteredVoices = availableVoices.filter { voice ->
            voice.locale.language.equals("en", ignoreCase = true)
        }

        val resultList = mutableListOf<SpeakerItem>()
        for (i in filteredVoices.indices) {
            resultList.add(
                SpeakerItem(
                    i,
                    filteredVoices[i].name,
                    filteredVoices[i].locale.toString(),
                    filteredVoices[i].quality,
                    filteredVoices[i].latency,
                    filteredVoices[i].isNetworkConnectionRequired
                )
            )
        }
        return resultList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }

    interface BottomSheetSpeakerListener {
        fun onSelectedSpeaker(selectedSpeaker: String)
    }

    fun setListener(listener: BottomSheetSpeakerListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickItem(item: SpeakerItem) {
        speakerSelected = item.name
        Log.d("ppppp", speakerSelected)
        playSpeaker(item.name)
    }

    private fun playSpeaker(speakerType: String) {
        val availableVoices = try {
            tts.voices
        } catch (e: Exception) {
            null
        } ?: return

        val voice = availableVoices.find { it.name == speakerType }
        val word = "This data suggests that higher education remains a priority for many students"

        if (voice != null) {
            tts.voice = voice
            tts.setSpeechRate(0.9f)
            tts.setPitch(1.1f)
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTS", "Voice $speakerType not found.")
        }
    }
}
