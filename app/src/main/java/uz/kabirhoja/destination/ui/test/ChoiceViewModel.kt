package uz.kabirhoja.destination.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uz.kabirhoja.destination.data.model.OptionItem
import uz.kabirhoja.destination.data.model.TestChoiceItem

class ChoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val defaultOptions = listOf(
        OptionItem("Topic vocabulary", true),
        OptionItem("Phrasal verbs", true),
        OptionItem("Prepositional phrases", true),
        OptionItem("Word patterns", true),
        OptionItem("Word formation", true),
        OptionItem("Test", true),
        OptionItem("Write", false)
    )

    private val defaultUnits = listOf(
        TestChoiceItem("Fun and games", 3, false),
        TestChoiceItem("Learning and doing", 6, false),
        TestChoiceItem("Coming and going", 9, false),
        TestChoiceItem("Friends and relations", 12, false),
        TestChoiceItem("Buying and selling", 15, false),
        TestChoiceItem("Inventions and discoveries", 18, false),
        TestChoiceItem("Sending and receiving", 21, false),
        TestChoiceItem("People and daily life", 24, false),
        TestChoiceItem("Working and earning", 27, false),
        TestChoiceItem("Body and lifestyle", 30, false),
        TestChoiceItem("Creating and building", 33, false),
        TestChoiceItem("Nature and the universe", 36, false),
        TestChoiceItem("Laughing and crying", 39, false),
        TestChoiceItem("Problems and solutions", 42, false)
    )

    private val _optionList = MutableLiveData<List<OptionItem>>(defaultOptions)
    val optionList: LiveData<List<OptionItem>> = _optionList

    private val _numberList = MutableLiveData<List<TestChoiceItem>>(defaultUnits)
    val numberList: LiveData<List<TestChoiceItem>> = _numberList

    fun getOptions(): List<OptionItem> {
        return _optionList.value ?: defaultOptions
    }

    fun getUnits(): List<TestChoiceItem> {
        return _numberList.value ?: defaultUnits
    }

    fun toggleCategory(index: Int) {
        val current = _optionList.value?.map { it.copy() } ?: defaultOptions.map { it.copy() }
        if (index in current.indices) {
            current[index].isChecked = !current[index].isChecked
            _optionList.value = current
        }
    }

    fun selectMode(mode: String) {
        val current = _optionList.value?.map { it.copy() } ?: defaultOptions.map { it.copy() }
        if (current.size >= 7) {
            if (mode == "Test") {
                current[5].isChecked = true
                current[6].isChecked = false
            } else if (mode == "Write") {
                current[5].isChecked = false
                current[6].isChecked = true
            }
            _optionList.value = current
        }
    }

    fun toggleUnit(item: TestChoiceItem) {
        val current = _numberList.value ?: defaultUnits
        val target = current.find { it.unitNumber == item.unitNumber }
        if (target != null) {
            target.checked = item.checked
        }
    }
}
