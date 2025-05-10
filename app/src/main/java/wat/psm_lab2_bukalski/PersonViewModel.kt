package wat.psm_lab2_bukalski

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonViewModel(private val dao: PersonDao) : ViewModel() {

    private val _personList = MutableStateFlow<List<Person>>(emptyList()) // Stan strumienia
    val personList: StateFlow<List<Person>> = _personList

    init {
        viewModelScope.launch {
            dao.getAllPersons().collect { persons ->
                _personList.value = persons // Aktualizacja listy
            }
        }
    }

    fun updatePerson(person: Person) {
        viewModelScope.launch {
            dao.updatePerson(person)
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch {
            dao.deletePerson(person.id)
        }
    }

}
