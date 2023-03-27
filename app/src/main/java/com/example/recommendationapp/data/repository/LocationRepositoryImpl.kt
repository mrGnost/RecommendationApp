package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.location.LocationSource
import com.example.recommendationapp.domain.model.Location
import com.example.recommendationapp.domain.repository.LocationRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class LocationRepositoryImpl
@Inject constructor(private val locationSource: LocationSource) : LocationRepository {
    override fun setLocationUpdates(): Completable {
        return Completable.fromRunnable {
            locationSource.setLocationUpdates()
        }
    }

    override fun getCurrentLocation(): Single<Location> {
        return Single.fromCallable {
            locationSource.getCurrentLocation().toEntity()
        }
    }
}