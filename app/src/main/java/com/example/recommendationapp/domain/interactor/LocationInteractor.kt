package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.repository.LocationRepository
import javax.inject.Inject

class LocationInteractor @Inject constructor(private val locationRepository: LocationRepository) {
    fun startUpdates() = locationRepository.setLocationUpdates()

    fun getCurrentLocation() = locationRepository.getCurrentLocation()
}