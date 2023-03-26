package com.example.recommendationapp.domain.interactor

import androidx.lifecycle.LiveData
import com.example.recommendationapp.domain.model.Filter
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

    fun makeFavourite(restaurants: List<RestaurantShort>) =
        databaseRepository.makeFavourite(restaurants)

    fun setLike(restaurant: RestaurantShort, value: Boolean) =
        databaseRepository.setLike(restaurant, value)

    fun setMark(restaurant: RestaurantShort, value: Boolean) =
        databaseRepository.setMark(restaurant, value)

    fun getRestaurantsInArea(
        recommended: Boolean,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ) = databaseRepository.getInArea(recommended, leftLat, leftLon, rightLat, rightLon)

    fun getRestaurants(favourite: Boolean) =
        databaseRepository.getRestaurants(favourite)

    fun findRestaurants(prefix: String) =
        databaseRepository.findRestaurants(prefix)

    fun putFilters(filters: List<Filter>) =
        databaseRepository.putFilters(filters)

    fun getFilters() = databaseRepository.getFilters()

    fun setFilterChecked(filter: Filter, checked: Boolean, filterId: Int) =
        databaseRepository.changeCheckedFilter(filter, checked, filterId)

    fun changeFavourite(id: Int, favourite: Boolean) =
        databaseRepository.changeFavourite(id, favourite)

    fun changeMarked(id: Int, marked: Boolean) =
        databaseRepository.changeMark(id, marked)
}