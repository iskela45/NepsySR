package fi.organization.nepsysr.database

import android.graphics.Bitmap
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ContactViewModel(private val repository: AppRepository) : ViewModel() {

    // Using LiveData and caching what allContacts returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allContacts: LiveData<List<Contact>> = repository.allContacts.asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(contact: Contact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun updateContactImage(uid: Int, img: Bitmap) = viewModelScope.launch {
        repository.updateContactImage(uid, img)
    }

    fun updateContact(uid: Int, name: String,
                      img: Bitmap, color: String) = viewModelScope.launch {
        repository.updateContact(uid, name, img, color)
    }
}
// Boilerplate code
class ContactViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
