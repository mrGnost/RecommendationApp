package com.example.recommendationapp.presentation.launcher.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.ActivityMainBinding
import com.example.recommendationapp.presentation.auth.view.AuthFragment
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.map.view.MapFragment
import com.example.recommendationapp.utils.Common.LOCATION_PERMISSION_REQUEST_CODE
import com.yandex.mapkit.MapKitFactory

class   LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComp().inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapKitFactory.initialize(this)
        setupBottomNav()
        checkPermission()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.places_nav_item -> {
                    return@setOnItemSelectedListener checkPermission()
                }
                R.id.fav_nav_item -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, FavouriteFragment.newInstance(), FavouriteFragment.TAG)
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.profile_nav_item -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, AuthFragment.newInstance(), AuthFragment.TAG)
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(LOCATION_PERMISSION_REQUEST_CODE)
            false
        } else {
            goToMap()
            true
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
        val bundle = Bundle()
        val latitude = intent.getDoubleExtra("latitude", 55.75)
        val longitude = intent.getDoubleExtra("longitude", 37.62)
        bundle.putDouble("latitude", latitude)
        bundle.putDouble("longitude", longitude)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), MapFragment.TAG)
            .commit()
        supportFragmentManager.setFragmentResult("location", bundle)
    }
}