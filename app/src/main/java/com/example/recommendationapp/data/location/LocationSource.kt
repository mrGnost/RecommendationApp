package com.example.recommendationapp.data.location

import com.example.recommendationapp.data.model.LocationDataEntity

interface LocationSource {
    fun setLocationUpdates()

    fun getCurrentLocation(): LocationDataEntity
}