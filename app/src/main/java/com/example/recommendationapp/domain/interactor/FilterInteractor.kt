package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.repository.FilterRepository
import javax.inject.Inject

class FilterInteractor @Inject constructor(private val filterRepository: FilterRepository) {
    fun setRecommendationFilter(value: Boolean) =
        filterRepository.setRecommendationFilter(value)

    fun getRecommendationFilter() =
        filterRepository.getRecommendationFilter()
}