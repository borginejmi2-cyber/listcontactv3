package com.example.listcontactv2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeContactsFragment : Fragment(R.layout.fragment_liste_contacts) {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val rvContacts = view.findViewById<RecyclerView>(R.id.rvContacts)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)

        adapter = ContactAdapter { contact ->
            viewModel.selectContact(contact)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DetailContactFragment())
                .addToBackStack(null)
                .commit()
        }

        rvContacts.layoutManager = LinearLayoutManager(requireContext())
        rvContacts.adapter = adapter

        // Filtrage au fur et à mesure de la saisie
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterContacts(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Observer la liste affichée
        viewModel.displayedContacts.observe(viewLifecycleOwner) { contacts ->
            adapter.updateContacts(contacts)
            if (contacts.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvContacts.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvContacts.visibility = View.VISIBLE
            }
        }
    }
}
