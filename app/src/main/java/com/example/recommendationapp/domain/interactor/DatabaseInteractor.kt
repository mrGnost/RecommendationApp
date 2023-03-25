package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.DatabaseRepository
import io.reactivex.Single
import javax.inject.Inject

class DatabaseInteractor @Inject constructor(private val databaseRepository: DatabaseRepository) {
    fun putRestaurants(restaurants: List<Restaurant>) =
        databaseRepository.putRestaurants(restaurants)

    fun putRestaurantsShort(restaurants: List<RestaurantShort>) =
        databaseRepository.putRestaurantsShort(restaurants)

    fun makeRecommended(restaurants: List<RestaurantShort>) =
        databaseRepository.makeRecommended(restaurants)

    fun getRestaurantsInArea(
        recommended: Boolean,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>> =
        databaseRepository.getInArea(recommended, leftLat, leftLon, rightLat, rightLon)
}