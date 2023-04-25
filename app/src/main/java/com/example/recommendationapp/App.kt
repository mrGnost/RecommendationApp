package com.example.recommendationapp

import android.app.Application
import com.example.recommendationapp.di.AppComponent
import com.example.recommendationapp.di.DaggerAppComponent
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.yandex.mapkit.MapKitFactory
import javax.inject.Inject

class App : Application() {
    private lateinit var appComponent: AppComponent

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("f09c2293-85b6-4329-a1dd-b9783e630711")
        appComponent = DaggerAppComponent.builder().context(applicationContext).build()
    }

    fun appComp() = appComponent
}