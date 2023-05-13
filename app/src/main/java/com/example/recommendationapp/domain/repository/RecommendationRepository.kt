package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.*
import io.reactivex.Single

interface RecommendationRepository {
    fun getAllRestaurants(): Single<List<RestaurantShort>>

    fun getRestaurant(placeId: Int): Single<Restaurant>

    fun getFilters(): Single<List<Filter>>

    fun getRecommended(userId: Int): Single<List<Int>>

    fun getRecommendedUnauthorized(favourites: List<Int>): Single<List<Int>>

    fun getFavourite(userId: Int): Single<List<RestaurantShort>>

    fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>>

    fun getFilteredPlaces(userId: Int, filters: List<Filter>, recommended: Boolean):
            Single<List<Int>>

    fun login(account: Account): Single<Int>

    fun register(account: Account): Single<Void>
}