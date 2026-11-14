package com.example.listcontactv2

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact> = emptyList(),
    private val onItemClick: (Contact) -> Unit,
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgContact: ImageView = itemView.findViewById(R.id.imgContact)
        private val tvNomPrenom: TextView = itemView.findViewById(R.id.tvNomPrenom)
        private val tvTelephone: TextView = itemView.findViewById(R.id.tvTelephone)

        fun bind(contact: Contact) {
            tvNomPrenom.text = "${contact.nom} ${contact.prenom}"
            tvTelephone.text = contact.telephone

            if (!contact.photoUri.isNullOrEmpty()) {
                try {
                    imgContact.setImageURI(Uri.parse(contact.photoUri))
                } catch (_: Exception) {
                    imgContact.setImageResource(R.mipmap.ic_launcher_round)
                }
            } else {
                imgContact.setImageResource(R.mipmap.ic_launcher_round)
            }

            itemView.setOnClickListener {
                onItemClick(contact)
            }
        }
    }
}
