package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import io.reactivex.Completable
import io.reactivex.Single

interface DatabaseRepository {
    fun putRestaurants(restaurants: List<Restaurant>): Completable

    fun putRestaurantsShort(restaurants: List<RestaurantShort>): Completable

    fun makeRecommended(restaurants: List<RestaurantShort>): Completable

    fun makeFavourite(restaurants: List<RestaurantShort>): Completable

    fun removeMark(restaurant: RestaurantShort): Completable

    fun getInArea(
        recommended: Boolean,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>>
}