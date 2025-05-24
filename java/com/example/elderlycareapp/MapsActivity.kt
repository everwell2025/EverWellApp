package com.example.elderlycareapp

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.os.Bundle
import com.example.elderlycareapp.StreetViewActivity // adjust this based on your actual package
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.StreetViewPanoramaFragment
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.model.StreetViewSource


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(24.8607, 67.0011) // Replace with your hometown coordinates


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))


        googleMap.setOnMapClickListener {
            openStreetView(it)
        }
    }

    private fun openStreetView(location: LatLng) {
        val intent = Intent(this, StreetViewActivity::class.java)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lng", location.longitude)
        startActivity(intent)
    }

}
