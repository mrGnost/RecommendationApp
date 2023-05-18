package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.api.RecommendationApi
import com.example.recommendationapp.data.model.AccountDataEntity
import com.example.recommendationapp.data.model.FilterDataEntityRequest
import com.example.recommendationapp.domain.model.*
import com.example.recommendationapp.domain.repository.RecommendationRepository
import com.example.recommendationapp.utils.Common.getTokenHeader
import io.reactivex.Completable
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

    override fun getRecommended(token: String): Single<List<Int>> {
        return api.getRecommended(getTokenHeader(token)).map { x -> x.map { it.id } }
    }

    override fun getRecommendedUnauthorized(favourites: List<Int>): Single<List<Int>> {
        return api.getRecommendedUnauthorized(favourites).map { x -> x.map { it.id } }
    }

    override fun getFavourite(token: String): Single<List<RestaurantShort>> {
        return api.getFavourite(getTokenHeader(token)).map { x -> x.map { it.toEntity() } }
    }

    override fun getMarked(token: String): Single<List<RestaurantShort>> {
        return api.getMarked(getTokenHeader(token)).map { x -> x.map { it.toEntity() } }
    }

    override fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>> {
        return api.getSimilarPlaces(placeId, amount).map { x -> x.map { it.toEntity() } }
    }

    override fun getFilteredPlaces(
        token: String,
        filters: List<Filter>,
        recommended: Boolean
    ): Single<List<Int>> {
        return if (recommended)
            api.getFilteredRecommendedPlaces(getTokenHeader(token), filters.map {
                FilterDataEntityRequest.fromEntity(it)
            })
        else
            api.getFilteredPlaces(filters.map {
                FilterDataEntityRequest.fromEntity(it)
            })
    }

    override fun addFavourites(token: String, cafes: List<Int>): Completable {
        return api.addFavourites(getTokenHeader(token), cafes)
    }

    override fun removeFavourites(token: String, cafes: List<Int>): Completable {
        return api.removeFavourites(getTokenHeader(token), cafes)
    }

    override fun addMarked(token: String, cafes: List<Int>): Completable {
        return api.addMarked(getTokenHeader(token), cafes)
    }

    override fun removeMarked(token: String, cafes: List<Int>): Completable {
        return api.removeMarked(getTokenHeader(token), cafes)
    }

    override fun login(account: Account): Single<Token> {
        return api.login(AccountDataEntity.fromEntity(account)).map { it.toEntity() }
    }

    override fun register(account: Account): Completable {
        return api.register(AccountDataEntity.fromEntity(account))
    }
}