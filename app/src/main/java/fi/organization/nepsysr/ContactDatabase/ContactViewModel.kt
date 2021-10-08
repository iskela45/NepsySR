package fi.organization.nepsysr.ContactDatabase

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

    // Using LiveData and caching what allContacts returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allContacts: LiveData<List<Contact>> = repository.allContacts.asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }
}

// Boilerplate code
class ContactViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
