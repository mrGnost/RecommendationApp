package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.Location
import io.reactivex.Completable
import io.reactivex.Single

interface LocationRepository {
    fun setLocationUpdates(): Completable

    fun getCurrentLocation(): Single<Location>
}