package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.*
import io.reactivex.Single

interface RecommendationRepository {
    fun getAllRestaurants(): Single<List<RestaurantShort>>

    fun getRestaurant(placeId: Int): Single<Restaurant>

    fun getFilters(): Single<List<Filter>>

    fun getRecommended(token: String): Single<List<Int>>

    fun getRecommendedUnauthorized(favourites: List<Int>): Single<List<Int>>

    fun getFavourite(token: String): Single<List<RestaurantShort>>

    fun getMarked(token: String): Single<List<RestaurantShort>>

    fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>>

    fun getFilteredPlaces(token: String, filters: List<Filter>, recommended: Boolean):
            Single<List<Int>>

    fun addFavourites(token: String, cafes: List<Int>): Single<Void>

    fun removeFavourites(token: String, cafes: List<Int>): Single<Void>

    fun addMarked(token: String, cafes: List<Int>): Single<Void>

    fun removeMarked(token: String, cafes: List<Int>): Single<Void>

    fun login(account: Account): Single<String>

    fun register(account: Account): Single<Void>
}