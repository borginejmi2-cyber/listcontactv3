package com.example.listcontactv2

import java.io.Serializable
import java.util.UUID

data class Contact(
    val id: String = UUID.randomUUID().toString(),
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
) : Serializable