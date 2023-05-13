package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.DatabaseRepository
import javax.inject.Inject

class DatabaseInteractor @Inject constructor(private val databaseRepository: DatabaseRepository) {
    fun putRestaurantsShort(restaurants: List<RestaurantShort>) =
        databaseRepository.putRestaurantsShort(restaurants)

    fun makeRecommended(restaurantIds: List<Int>) =
        databaseRepository.makeRecommended(restaurantIds)

    fun makeFavourite(restaurantIds: List<Int>) =
        databaseRepository.makeFavourite(restaurantIds)

    fun setLike(restaurantId: Int, value: Boolean) =
        databaseRepository.setLike(restaurantId, value)

    fun setMark(restaurantId: Int, value: Boolean) =
        databaseRepository.setMark(restaurantId, value)

    fun getRestaurantsInArea(
        recommendedIds: List<Int>,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ) = databaseRepository.getInArea(recommendedIds, leftLat, leftLon, rightLat, rightLon)

    fun getRestaurantsByIds(ids: List<Int>) = databaseRepository.getRestaurantsByIds(ids)

    fun getRestaurantIds(favourite: Boolean) =
        databaseRepository.getRestaurantIds(favourite)

    fun findRestaurants(prefix: String) =
        databaseRepository.findRestaurants(prefix)

    fun putFilters(filters: List<Filter>) =
        databaseRepository.putFilters(filters)

    fun getFilters() = databaseRepository.getFilters()

    fun getRecommendedCount() = databaseRepository.getRecommendedCount()

    fun getFiltersCount() = databaseRepository.getFiltersCount()

    fun clearFilters(filters: List<Filter>) = databaseRepository.clearFilters(filters)

    fun setFilterChecked(filter: Filter, checked: Boolean, filterId: Int) =
        databaseRepository.changeCheckedFilter(filter, checked, filterId)

    fun checkIfRecommended(id: Int) = databaseRepository.checkIfRecommended(id)

    fun checkIfFavourite(id: Int) = databaseRepository.checkIfFavourite(id)

    fun checkIfMarked(id: Int) = databaseRepository.checkIfMarked(id)
}