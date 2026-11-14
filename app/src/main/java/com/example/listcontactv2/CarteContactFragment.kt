package com.example.listcontactv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment(R.layout.fragment_carte_contact), OnMapReadyCallback {

    private val viewModel: ContactViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val btnOpenExternalMap = view.findViewById<Button>(R.id.btnOpenExternalMap)
        btnOpenExternalMap.setOnClickListener {
            val contact = viewModel.selectedContact.value
            val lat = contact?.latitude
            val lon = contact?.longitude

            if (contact != null && lat != null && lon != null) {
                val label = "${contact.nom} ${contact.prenom}"
                val geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon(${Uri.encode(label)})")
                val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                try {
                    startActivity(mapIntent)
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Aucune application de carte disponible", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Coordonnées GPS invalides", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val contact = viewModel.selectedContact.value
        val lat = contact?.latitude
        val lon = contact?.longitude

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (contact != null && lat != null && lon != null) {
            val position = LatLng(lat, lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("${contact.nom} ${contact.prenom}")
                    .snippet("Lat: $lat, Lon: $lon")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14f))
        } else {
            Toast.makeText(
                requireContext(),
                "Coordonnées GPS non disponibles pour ce contact",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
