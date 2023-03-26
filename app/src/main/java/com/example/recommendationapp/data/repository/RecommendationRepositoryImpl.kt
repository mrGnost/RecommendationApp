package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.api.RecommendationApi
import com.example.recommendationapp.domain.model.AllRestaurantsResponse
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.RecommendationRepository
import io.reactivex.Single
import javax.inject.Inject

class RecommendationRepositoryImpl
@Inject constructor(private val api: RecommendationApi) : RecommendationRepository {
    override fun getAllRestaurants(): Single<List<RestaurantShort>> {
        return api.getAllRestaurants().map { x -> x.map { it.toEntity() } }
    }

    override fun getRestaurant(placeId: Int): Single<Restaurant> {
        return api.getRestaurantInfo(placeId).map { it.toEntity() }
    }

    override fun getFilters(): Single<List<Filter>> {
        return api.getFilters().map { x -> x.map{ it.toEntity() } }
    }

    override fun getRecommended(userId: Int): Single<List<RestaurantShort>> {
        return api.getRecommended(userId).map { x -> x.map { it.toEntity() } }
    }

    override fun getFavourite(userId: Int): Single<List<RestaurantShort>> {
        return api.getFavourite(userId).map { x -> x.map { it.toEntity() } }
    }
}