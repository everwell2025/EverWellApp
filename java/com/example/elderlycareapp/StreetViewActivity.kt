package com.example.elderlycareapp

import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


class StreetViewActivity : BaseActivity(), OnStreetViewPanoramaReadyCallback {

    private lateinit var etLocation: EditText
    private lateinit var btnSearch: Button
    private lateinit var streetViewPanoramaFragment: StreetViewPanoramaFragment
    private var panorama: StreetViewPanorama? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        setupBottomNavigation(R.id.nav_map)         // For StreetViewActivity

        etLocation = findViewById(R.id.et_location)
        btnSearch = findViewById(R.id.btn_search)


        val fragment = fragmentManager.findFragmentById(R.id.streetviewpanorama) as? StreetViewPanoramaFragment
        fragment?.getStreetViewPanoramaAsync { svp ->
            panorama = svp
        } ?: run {
            Toast.makeText(this, "Failed to load Street View fragment", Toast.LENGTH_SHORT).show()
        }


        btnSearch.setOnClickListener {
            val locationName = etLocation.text.toString()
            if (locationName.isNotEmpty()) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addressList = geocoder.getFromLocationName(locationName, 1)


                if (!addressList.isNullOrEmpty()) {
                    val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                    panorama?.setPosition(latLng) ?: Toast.makeText(this, "Panorama not ready", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStreetViewPanoramaReady(panorama: StreetViewPanorama) {
        this.panorama = panorama
        // Optional: set default location (like your university or favorite landmark)
        panorama.setPosition(LatLng(25.0754, 55.14)) // Default fallback
    }
}
