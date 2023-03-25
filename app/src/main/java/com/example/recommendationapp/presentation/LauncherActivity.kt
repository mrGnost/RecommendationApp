package com.example.recommendationapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.recommendationapp.R
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.map.view.MapFragment
import com.example.recommendationapp.utils.Common.LOCATION_PERMISSION_REQUEST_CODE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MapKitFactory.initialize(this)
        setupBottomNav()
        checkPermission()
    }

    private fun setupBottomNav() {
        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.places_nav_item -> {
                    checkPermission()
                }
                R.id.fav_nav_item -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, FavouriteFragment.newInstance(), FavouriteFragment.TAG)
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            goToMap()
        }
    }

    private fun requestPermission(code: Int) {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), code
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToMap()
            }
        }
    }

    private fun goToMap() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), MapFragment.TAG)
            .commit()
    }
}