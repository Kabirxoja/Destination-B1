package uz.kabirhoja.destination.ui.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony.Mms.Intents
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uz.kabirhoja.destination.data.repository.MainViewModelFactory
import com.kabirhoja.destination.databinding.FragmentSettingsBinding
import uz.kabirhoja.destination.custom.AnimationButton.animateClick
import uz.kabirhoja.destination.ui.additions.BottomSheetLanguage
import uz.kabirhoja.destination.ui.additions.BottomSheetSpeaker
import uz.kabirhoja.destination.ui.additions.MainSharedPreference
import uz.kabirhoja.destination.viewmodel.SettingsViewModel

class SettingsFragment : Fragment(),
    BottomSheetLanguage.BottomSheetLanguageListener,
    BottomSheetSpeaker.BottomSheetSpeakerListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var bottomSheetLanguage: BottomSheetLanguage
    private lateinit var bottomSheetSpeaker: BottomSheetSpeaker


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireActivity().application)
        )[SettingsViewModel::class.java]


        val selectedLanguage = MainSharedPreference.getLanguage(requireContext())
        val selectedSpeaker = MainSharedPreference.getSpeakerType(requireContext())
        Log.d("SettingsFragment", "Selected Language: $selectedLanguage")
        Log.d("SettingsFragment", "Selected Speaker: $selectedSpeaker")

        // Observe language changes
        settingsViewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            when(language){
                "ka" -> binding.txtLanguageChose.text = "Karakalpak"
                "uz" -> binding.txtLanguageChose.text = "Uzbek"
            }
        }

        // Observe speaker changes
        settingsViewModel.currentSpeaker.observe(viewLifecycleOwner) { speaker ->
            binding.txtVoiceChose.text = when {
                speaker.contains("gb") -> "UK"
                speaker.contains("au") -> "AUS"
                speaker.contains("us") -> "USA"
                else -> speaker
            }
        }


        binding.layoutLanguage.setOnClickListener {
            bottomSheetLanguage = BottomSheetLanguage()
            bottomSheetLanguage.setListener(this)
            bottomSheetLanguage.show(childFragmentManager, "BottomSheet")
            it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
        }

        binding.layoutVoice.setOnClickListener {
            bottomSheetSpeaker = BottomSheetSpeaker()
            bottomSheetSpeaker.setListener(this)
            bottomSheetSpeaker.show(childFragmentManager, "BottomSheet")
            it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
        }

        binding.layoutShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.kabirhoja.destination")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
            it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
        }

        binding.layoutRate.setOnClickListener {
            val appPackageName = "com.kabirhoja.destination"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
            }
        }

        binding.layoutExit.setOnClickListener {
            requireActivity().finish()
            it.animateClick(scaleNormal = 1.0f, scaleShrink = 0.96f)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSelectedLanguage(selectedLanguage: String) {
        settingsViewModel.updateLanguage(selectedLanguage)

    }

    override fun onSelectedSpeaker(selectedSpeaker: String) {
        settingsViewModel.updateSpeaker(selectedSpeaker)
    }


}