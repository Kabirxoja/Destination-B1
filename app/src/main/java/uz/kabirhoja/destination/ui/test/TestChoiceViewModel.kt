package uz.kabirhoja.destination.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import uz.kabirhoja.destination.data.model.Vocabulary
import uz.kabirhoja.destination.data.repository.MainRepository
import kotlinx.coroutines.launch

class TestChoiceViewModel(application: Application, private val repository: MainRepository) : AndroidViewModel(application) {

    private val _getOptions = MutableLiveData<List<Vocabulary>>()
    val getOptions: LiveData<List<Vocabulary>> get() = _getOptions

    fun setOptions(units: List<Int>, types: List<String>) {
        viewModelScope.launch {
            val words = repository.getFilteredWords(units, types)
            _getOptions.postValue(words)
        }
    }


}
