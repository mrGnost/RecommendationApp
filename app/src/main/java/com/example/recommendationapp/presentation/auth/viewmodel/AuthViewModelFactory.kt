package com.example.recommendationapp.presentation.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.utils.scheduler.SchedulerProvider

class AuthViewModelFactory(
    private val recommendationInteractor: RecommendationInteractor,
    private val localInteractor: LocalInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            recommendationInteractor, localInteractor, databaseInteractor, schedulerProvider
        ) as T
    }
}