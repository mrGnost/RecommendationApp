package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.AllRestaurantsResponse
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import io.reactivex.Single

interface RecommendationRepository {
    fun getAllRestaurants(): Single<List<RestaurantShort>>

    fun getRestaurant(placeId: Int): Single<Restaurant>

    fun getFilters(): Single<List<Filter>>

    fun getRecommended(userId: Int): Single<List<RestaurantShort>>

    fun getFavourite(userId: Int): Single<List<RestaurantShort>>

    fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>>
}