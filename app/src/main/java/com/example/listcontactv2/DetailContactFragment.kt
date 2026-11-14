package com.example.listcontactv2

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.ByteArrayOutputStream

class DetailContactFragment : Fragment(R.layout.fragment_detail_contact) {

    private val viewModel: ContactViewModel by activityViewModels()

    private lateinit var imgPhoto: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTelephone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText

    private var currentPhotoUri: String? = null
    private var currentContact: Contact? = null

    // Launcher pour la galerie
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it.toString()
            imgPhoto.setImageURI(it)
        }
    }

    // Launcher pour la caméra
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val bytes = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                it,
                "Contact_${System.currentTimeMillis()}",
                null
            )
            currentPhotoUri = path
            imgPhoto.setImageBitmap(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgPhoto = view.findViewById(R.id.imgDetailPhoto)
        etNom = view.findViewById(R.id.etDetailNom)
        etPrenom = view.findViewById(R.id.etDetailPrenom)
        etTelephone = view.findViewById(R.id.etDetailTelephone)
        etEmail = view.findViewById(R.id.etDetailEmail)
        etLatitude = view.findViewById(R.id.etDetailLatitude)
        etLongitude = view.findViewById(R.id.etDetailLongitude)

        val btnGallery = view.findViewById<Button>(R.id.btnGallery)
        val btnCamera = view.findViewById<Button>(R.id.btnCamera)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnCall = view.findViewById<Button>(R.id.btnCall)
        val btnEmail = view.findViewById<Button>(R.id.btnEmail)
        val btnMap = view.findViewById<Button>(R.id.btnMap)

        // Remplissage du formulaire avec le contact sélectionné
        viewModel.selectedContact.observe(viewLifecycleOwner) { contact ->
            currentContact = contact
            contact?.let {
                etNom.setText(it.nom)
                etPrenom.setText(it.prenom)
                etTelephone.setText(it.telephone)
                etEmail.setText(it.email)
                etLatitude.setText(it.latitude?.toString().orEmpty())
                etLongitude.setText(it.longitude?.toString().orEmpty())
                currentPhotoUri = it.photoUri

                if (!currentPhotoUri.isNullOrEmpty()) {
                    try {
                        imgPhoto.setImageURI(Uri.parse(currentPhotoUri))
                    } catch (_: Exception) {
                        imgPhoto.setImageResource(R.mipmap.ic_launcher_round)
                    }
                } else {
                    imgPhoto.setImageResource(R.mipmap.ic_launcher_round)
                }
            }
        }

        btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        btnSave.setOnClickListener {
            val nom = etNom.text.toString().trim()
            val prenom = etPrenom.text.toString().trim()
            val telephone = etTelephone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val latStr = etLatitude.text.toString().trim()
            val lonStr = etLongitude.text.toString().trim()

            if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez remplir les champs obligatoires (*)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var latitude: Double? = null
            if (latStr.isNotEmpty()) {
                latitude = latStr.toDoubleOrNull()
                if (latitude == null || latitude < -90.0 || latitude > 90.0) {
                    Toast.makeText(requireContext(), "Latitude invalide (doit être entre -90 et 90)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            var longitude: Double? = null
            if (lonStr.isNotEmpty()) {
                longitude = lonStr.toDoubleOrNull()
                if (longitude == null || longitude < -180.0 || longitude > 180.0) {
                    Toast.makeText(requireContext(), "Longitude invalide (doit être entre -180 et 180)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val contactToSave = currentContact?.copy(
                nom = nom,
                prenom = prenom,
                telephone = telephone,
                email = email,
                photoUri = currentPhotoUri,
                latitude = latitude,
                longitude = longitude
            ) ?: Contact(
                nom = nom,
                prenom = prenom,
                telephone = telephone,
                email = email,
                photoUri = currentPhotoUri,
                latitude = latitude,
                longitude = longitude
            )

            viewModel.updateContact(contactToSave)
            Toast.makeText(requireContext(), "Contact enregistré", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnCall.setOnClickListener {
            val tel = etTelephone.text.toString().trim()
            if (tel.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Aucun numéro de téléphone spécifié", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val mail = etEmail.text.toString().trim()
            if (mail.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$mail"))
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Aucune adresse e-mail spécifiée", Toast.LENGTH_SHORT).show()
            }
        }

        btnMap.setOnClickListener {
            val latStr = etLatitude.text.toString().trim()
            val lonStr = etLongitude.text.toString().trim()

            val lat = latStr.toDoubleOrNull()
            val lon = lonStr.toDoubleOrNull()

            if (lat == null || lon == null || lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
                Toast.makeText(
                    requireContext(),
                    "Impossible d'afficher la carte : Coordonnées GPS absentes ou invalides",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // S'assurer que le contact du ViewModel a les coordonnées saisies
                currentContact?.let {
                    it.latitude = lat
                    it.longitude = lon
                    viewModel.selectContact(it)
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CarteContactFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}
