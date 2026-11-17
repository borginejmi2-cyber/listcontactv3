package com.example.listcontactv2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class CarteContactFragment : Fragment(R.layout.fragment_carte_contact) {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = requireContext().applicationContext
        val prefs = ctx.getSharedPreferences("${ctx.packageName}_preferences", Context.MODE_PRIVATE)
        Configuration.getInstance().load(ctx, prefs)
        Configuration.getInstance().userAgentValue = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)

        val esriTileSource = object : OnlineTileSourceBase(
            "EsriWorldStreetMap",
            0,
            19,
            256,
            ".png",
            arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
                        MapTileIndex.getY(pMapTileIndex) + "/" +
                        MapTileIndex.getX(pMapTileIndex)
            }
        }

        mapView.setTileSource(esriTileSource)
        mapView.setMultiTouchControls(true)

        val contact = viewModel.selectedContact.value
        val lat = contact?.latitude
        val lon = contact?.longitude

        if (contact != null && lat != null && lon != null) {
            val startPoint = GeoPoint(lat, lon)
            mapView.controller.setZoom(16.0)
            mapView.controller.setCenter(startPoint)

            val marker = Marker(mapView)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "${contact.nom} ${contact.prenom}"
            marker.snippet = "Lat: $lat, Lon: $lon"
            mapView.overlays.add(marker)
        } else {
            Toast.makeText(
                requireContext(),
                "Coordonnées GPS non disponibles pour ce contact",
                Toast.LENGTH_LONG
            ).show()
        }

        val btnOpenExternalMap = view.findViewById<Button>(R.id.btnOpenExternalMap)
        btnOpenExternalMap.setOnClickListener {
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

    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mapView.isInitialized) {
            mapView.onPause()
        }
    }
}
