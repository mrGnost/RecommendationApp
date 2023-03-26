package com.example.recommendationapp.presentation.restaurant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModel
import com.example.recommendationapp.utils.scheduler.SchedulerProvider

class RestaurantViewModelFactory(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RestaurantViewModel(recommendationInteractor, databaseInteractor, schedulerProvider) as T
    }
}