package com.example.recommendationapp.domain.repository

import androidx.lifecycle.LiveData
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import io.reactivex.Completable
import io.reactivex.Single

interface DatabaseRepository {
    fun putRestaurants(restaurants: List<Restaurant>): Completable

    fun putRestaurantsShort(restaurants: List<RestaurantShort>): Completable

    fun makeRecommended(restaurants: List<RestaurantShort>): Completable

    fun makeFavourite(restaurants: List<RestaurantShort>): Completable

    fun setLike(restaurant: RestaurantShort, check: Boolean): Completable

    fun setMark(restaurant: RestaurantShort, check: Boolean): Completable

    fun getInArea(
        recommended: Boolean,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>>

    fun getRestaurants(favourite: Boolean): LiveData<List<RestaurantShort>>

    fun findRestaurants(prefix: String): Single<List<RestaurantShort>>
}