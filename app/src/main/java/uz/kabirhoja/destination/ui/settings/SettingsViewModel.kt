package uz.kabirhoja.destination.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import uz.kabirhoja.destination.data.local.MainSharedPreference

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {

    val currentLanguage = MutableLiveData<String>().apply {
        value = MainSharedPreference.getLanguage(application)
    }

    val currentSpeaker = MutableLiveData<String>().apply {
        value = MainSharedPreference.getSpeakerType(application)
    }

    fun updateLanguage(newLanguage: String) {
        MainSharedPreference.saveLanguage(application, newLanguage)
        currentLanguage.value = newLanguage
    }

    fun updateSpeaker(newSpeaker: String) {
        MainSharedPreference.saveSpeakerType(application, newSpeaker)
        currentSpeaker.value = newSpeaker
    }

}
