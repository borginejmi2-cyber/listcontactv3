package com.example.listcontactv2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {

    private val _allContacts = mutableListOf<Contact>()

    private val _displayedContacts = MutableLiveData<List<Contact>>()
    val displayedContacts: LiveData<List<Contact>> = _displayedContacts

    private val _selectedContact = MutableLiveData<Contact?>()
    val selectedContact: LiveData<Contact?> = _selectedContact

    private var currentFilterQuery: String = ""

    init {
        val initialList = listOf(
            Contact(
                nom = "Dupont",
                prenom = "Claire",
                telephone = "06 12 34 56 78",
                email = "claire.dupont@example.com",
                latitude = 48.8566,
                longitude = 2.3522,
            ),
            Contact(
                nom = "Martin",
                prenom = "Thomas",
                telephone = "07 23 45 67 89",
                email = "thomas.martin@example.com",
                latitude = 45.7640,
                longitude = 4.8357,
            ),
            Contact(
                nom = "Bernard",
                prenom = "Sophie",
                telephone = "06 98 76 54 32",
                email = "sophie.bernard@example.com",
                latitude = 43.2965,
                longitude = 5.3698,
            ),
            Contact(
                nom = "Petit",
                prenom = "Lucas",
                telephone = "07 11 22 33 44",
                email = "lucas.petit@example.com",
                latitude = null,
                longitude = null,
            ),
            Contact(
                nom = "Moreau",
                prenom = "Julien",
                telephone = "06 55 44 33 22",
                email = "julien.moreau@example.com",
                latitude = 43.6047,
                longitude = 1.4442,
            )
        )
        _allContacts.addAll(initialList)
        applyFilter(currentFilterQuery)
    }

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
    }

    fun filterContacts(query: String) {
        currentFilterQuery = query
        applyFilter(query)
    }

    private fun applyFilter(query: String) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            _displayedContacts.value = _allContacts.toList()
        } else {
            _displayedContacts.value = _allContacts.filter { contact ->
                contact.nom.lowercase().contains(q) ||
                contact.prenom.lowercase().contains(q) ||
                contact.telephone.lowercase().contains(q)
            }
        }
    }

    fun updateContact(updatedContact: Contact) {
        val index = _allContacts.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            _allContacts[index] = updatedContact
        } else {
            _allContacts.add(updatedContact)
        }
        _selectedContact.value = updatedContact
        applyFilter(currentFilterQuery)
    }
}
