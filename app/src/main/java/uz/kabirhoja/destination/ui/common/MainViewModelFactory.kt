package uz.kabirhoja.destination.ui.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.kabirhoja.destination.data.local.AppDatabase
import uz.kabirhoja.destination.data.repository.MainRepository
import uz.kabirhoja.destination.ui.home.HomeViewModel
import uz.kabirhoja.destination.ui.notes.NotesViewModel
import uz.kabirhoja.destination.ui.search.SearchViewModel
import uz.kabirhoja.destination.ui.settings.SettingsViewModel
import uz.kabirhoja.destination.ui.test.ChoiceViewModel
import uz.kabirhoja.destination.ui.test.ResultViewModel
import uz.kabirhoja.destination.ui.test.TestChoiceViewModel
import uz.kabirhoja.destination.ui.test.TestWriteViewModel
import uz.kabirhoja.destination.ui.vocabulary.VocabularyViewModel

class MainViewModelFactory(private var application: Application) : ViewModelProvider.Factory {

    val dao = AppDatabase.getDatabase(application).vocabularyDao()
    val repository = MainRepository(dao, application)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(application, repository) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application, repository) as T
        }

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(application, repository) as T
        }

        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application) as T
        }

        if (modelClass.isAssignableFrom(ChoiceViewModel::class.java)) {
            return ChoiceViewModel(application) as T
        }

        if (modelClass.isAssignableFrom(TestWriteViewModel::class.java)) {
            return TestWriteViewModel(application, repository) as T
        }

        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
            return VocabularyViewModel(application, repository) as T
        }

        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(TestChoiceViewModel::class.java)) {
            return TestChoiceViewModel(application, repository) as T
        }


        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
