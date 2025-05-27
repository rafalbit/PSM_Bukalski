package wat.psm_lab2_bukalski

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PersonViewModel(private val dao: PersonDao) : ViewModel() {

    private val _personList = MutableStateFlow<List<Person>>(emptyList()) // Stan strumienia

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
    suspend fun getUserByFullName(name: String, surname: String): Person? {
        return dao.getByFullName(name, surname)
    }

    fun addPerson(name: String, surname: String) {
        viewModelScope.launch {
            dao.insert(Person(0, name, surname))
        }
    }
    fun searchPeople(query: String, onResult: (List<Person>) -> Unit) {
        viewModelScope.launch {
            val result = dao.searchByNameOrSurname(query)
            onResult(result)
        }
    }
    fun loadAll(onResult: (List<Person>) -> Unit) {
        viewModelScope.launch {
            val result = dao.getAll()
            onResult(result)
        }
    }

    fun search(query: String, onResult: (List<Person>) -> Unit) {
        viewModelScope.launch {
            val result = if (query.isBlank()) {
                dao.getAll()
            } else {
                dao.searchByNameOrSurname(query)
            }
            onResult(result)
        }
    }


}
