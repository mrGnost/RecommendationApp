package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.api.RecommendationApi
import com.example.recommendationapp.data.model.AccountDataEntity
import com.example.recommendationapp.data.model.FilterDataEntityRequest
import com.example.recommendationapp.domain.model.*
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

    override fun getRecommended(userId: Int): Single<List<Int>> {
        return api.getRecommended(userId).map { x -> x.map { it.id } }
    }

    override fun getRecommendedUnauthorized(favourites: List<Int>): Single<List<Int>> {
        return api.getRecommendedUnauthorized(favourites).map { x -> x.map { it.id } }
    }

    override fun getFavourite(userId: Int): Single<List<RestaurantShort>> {
        return api.getFavourite(userId).map { x -> x.map { it.toEntity() } }
    }

    override fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>> {
        return api.getSimilarPlaces(placeId, amount).map { x -> x.map { it.toEntity() } }
    }

    override fun getFilteredPlaces(
        userId: Int,
        filters: List<Filter>,
        recommended: Boolean
    ): Single<List<Int>> {
        return if (recommended)
            api.getFilteredRecommendedPlaces(userId, filters.map {
                FilterDataEntityRequest.fromEntity(it)
            })
        else
            api.getFilteredPlaces(filters.map {
                FilterDataEntityRequest.fromEntity(it)
            })
    }

    override fun login(account: Account): Single<Int> {
        return api.login(AccountDataEntity.fromEntity(account))
    }

    override fun register(account: Account): Single<Void> {
        return api.register(AccountDataEntity.fromEntity(account))
    }
}