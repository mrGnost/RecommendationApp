package com.example.recommendationapp.data.location

import android.os.Looper
import android.util.Log
import com.example.recommendationapp.data.model.LocationDataEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import javax.inject.Inject

class LocationSourceImpl
@Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest
) : LocationSource {
    private var latitude = .0
    private var longitude = .0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                latitude = it.latitude
                longitude = it.longitude
            } ?: {
                Log.d("LOCATION", "Location information isn't available.")
            }
        }
    }

    override fun setLocationUpdates() {
        locationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    override fun getCurrentLocation(): LocationDataEntity {
        return LocationDataEntity(latitude, longitude)
    }
}