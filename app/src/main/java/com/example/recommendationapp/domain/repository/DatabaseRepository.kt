package com.example.recommendationapp.domain.repository

import androidx.lifecycle.LiveData
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import io.reactivex.Completable
import io.reactivex.Single

interface DatabaseRepository {

    fun putRestaurantsShort(restaurants: List<RestaurantShort>): Completable

    fun makeRecommended(ids: List<Int>): Completable

    fun makeFavourite(ids: List<Int>): Completable

    fun setLike(id: Int, check: Boolean): Completable

    fun setMark(id: Int, check: Boolean): Completable

    fun getInArea(
        recommendedIds: List<Int>,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>>

    fun getRestaurantsByIds(ids: List<Int>): Single<List<RestaurantShort>>

    fun getRestaurantIds(favourite: Boolean): LiveData<List<Int>>

    fun findRestaurants(prefix: String): Single<List<RestaurantShort>>

    fun putFilters(filters: List<Filter>): Completable

    fun getFilters(): LiveData<List<Filter>>

    fun clearFilters(filters: List<Filter>): Completable

    fun changeCheckedFilter(filter: Filter, value: Boolean, filterId: Int): Completable

    fun getRecommendedCount(): Single<Int>

    fun getFiltersCount(): LiveData<Int>

    fun checkIfRecommended(id: Int): Single<Boolean>

    fun checkIfFavourite(id: Int): Single<Boolean>

    fun checkIfMarked(id: Int): Single<Boolean>
}