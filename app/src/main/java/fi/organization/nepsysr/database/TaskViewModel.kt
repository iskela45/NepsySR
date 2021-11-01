package fi.organization.nepsysr.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: AppRepository) : ViewModel() {

    // Using LiveData and caching what allContacts returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    fun getFilteredList(filter: Int): LiveData<List<Task>> {
        return Transformations.map(allTasks) {
            it.filter {
                it.contactId == filter
            }
        }
    }

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAllTasks()
    }
}

// Boilerplate code
class TaskViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
