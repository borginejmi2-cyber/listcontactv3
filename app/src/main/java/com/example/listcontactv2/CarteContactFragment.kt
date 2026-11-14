package com.example.listcontactv2

import android.os.Bundle
import android.view.View
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val contact = viewModel.selectedContact.value
        val lat = contact?.latitude
        val lon = contact?.longitude

        if (contact != null && lat != null && lon != null) {
            val position = LatLng(lat, lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("${contact.nom} ${contact.prenom}")
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
