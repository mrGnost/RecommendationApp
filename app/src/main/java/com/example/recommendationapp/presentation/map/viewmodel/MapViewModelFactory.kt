package com.example.recommendationapp.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocationInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.utils.scheduler.SchedulerProvider

class MapViewModelFactory(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val locationInteractor: LocationInteractor,
    private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(
            recommendationInteractor, databaseInteractor, locationInteractor, schedulerProvider
        ) as T
    }
}