package com.example.recommendationapp

import android.app.Application
import com.example.recommendationapp.di.AppComponent
import com.example.recommendationapp.di.DaggerAppComponent
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("f09c2293-85b6-4329-a1dd-b9783e630711")
        appComponent = DaggerAppComponent.builder().context(applicationContext).build()
    }

    fun appComp() = appComponent
}