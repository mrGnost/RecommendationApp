package com.example.recommendationapp.presentation.launcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.domain.interactor.LocationInteractor
import com.example.recommendationapp.presentation.splash.viewmodel.SplashViewModel
import com.example.recommendationapp.utils.scheduler.SchedulerProvider

class LauncherViewModelFactory(
    private val locationInteractor: LocationInteractor,
    private val schedulers: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LauncherViewModel(locationInteractor, schedulers) as T
    }
}