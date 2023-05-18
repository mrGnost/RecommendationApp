package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.RecommendationRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class RecommendationInteractor
@Inject constructor(private val recommendationRepository: RecommendationRepository) {
    fun getAllRestaurants(): Single<List<RestaurantShort>> {
        return recommendationRepository.getAllRestaurants()
    }

    fun getRestaurant(id: Int): Single<Restaurant> {
        return recommendationRepository.getRestaurant(id)
    }

    fun getFilters(): Single<List<Filter>> {
        return recommendationRepository.getFilters()
    }

    fun getRecommended(token: String): Single<List<Int>> {
        return recommendationRepository.getRecommended(token)
    }

    fun getRecommendedUnauthorized(favourites: List<Int>): Single<List<Int>> {
        return recommendationRepository.getRecommendedUnauthorized(favourites)
    }

    fun getFavourite(token: String): Single<List<RestaurantShort>> {
        return recommendationRepository.getFavourite(token)
    }

    fun getMarked(token: String): Single<List<RestaurantShort>> {
        return recommendationRepository.getMarked(token)
    }

    fun getSimilar(placeId: Int, amount: Int): Single<List<RestaurantShort>> {
        return recommendationRepository.getSimilar(placeId, amount)
    }

    fun getFilteredPlaces(
        token: String,
        filters: List<Filter>,
        recommended: Boolean
    ) = recommendationRepository.getFilteredPlaces(token, filters, recommended)

    fun addFavourites(token: String, cafes: List<Int>): Completable {
        return recommendationRepository.addFavourites(token, cafes)
    }

    fun removeFavourites(token: String, cafes: List<Int>): Completable {
        return recommendationRepository.removeFavourites(token, cafes)
    }

    fun addMarked(token: String, cafes: List<Int>): Completable {
        return recommendationRepository.addMarked(token, cafes)
    }

    fun removeMarked(token: String, cafes: List<Int>): Completable {
        return recommendationRepository.removeMarked(token, cafes)
    }

    fun login(account: Account) = recommendationRepository.login(account)

    fun register(account: Account) = recommendationRepository.register(account)
}