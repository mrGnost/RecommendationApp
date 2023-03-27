package com.example.recommendationapp.presentation.launcher.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.ActivityMainBinding
import com.example.recommendationapp.domain.interactor.LocationInteractor
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.launcher.viewmodel.LauncherViewModel
import com.example.recommendationapp.presentation.launcher.viewmodel.LauncherViewModelFactory
import com.example.recommendationapp.presentation.map.view.MapFragment
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.presentation.splash.viewmodel.SplashViewModel
import com.example.recommendationapp.presentation.splash.viewmodel.SplashViewModelFactory
import com.example.recommendationapp.utils.Common.LOCATION_PERMISSION_REQUEST_CODE
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory
import javax.inject.Inject

class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LauncherViewModel

    @Inject
    lateinit var locationInteractor: LocationInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComp().inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createViewModel()
        observeLiveData()
        MapKitFactory.initialize(this)
        setupBottomNav()
        checkPermission()
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            LauncherViewModelFactory(locationInteractor, schedulers)
        )[LauncherViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getProgressLiveData().observe(this, this::showProgress)
        viewModel.getErrorLiveData().observe(this, this::showError)
    }

    private fun showProgress(isVisible: Boolean) {
        Log.i(SplashActivity.TAG, "showProgress called with param = $isVisible")
        // binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = ${throwable.stackTraceToString()}")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener {
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
        viewModel.startLocationUpdates()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, MapFragment.newInstance(), MapFragment.TAG)
            .commit()
    }
}