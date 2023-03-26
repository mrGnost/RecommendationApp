package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.RecommendationRepository
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

    fun getRecommended(userId: Int): Single<List<RestaurantShort>> {
        return recommendationRepository.getRecommended(userId)
    }

    fun getFavourite(userId: Int): Single<List<RestaurantShort>> {
        return recommendationRepository.getFavourite(userId)
    }
}