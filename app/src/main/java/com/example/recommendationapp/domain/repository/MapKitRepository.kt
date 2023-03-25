package com.example.recommendationapp.domain.repository

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.SearchOptions

interface MapKitRepository {
    fun search(query: String, startPoint: Point, options: SearchOptions)
}