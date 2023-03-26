package com.example.recommendationapp.presentation.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModel
import com.example.recommendationapp.utils.scheduler.SchedulerProvider

class FavouriteViewModelFactory(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(recommendationInteractor, databaseInteractor, schedulerProvider) as T
    }
}