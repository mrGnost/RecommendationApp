package com.example.recommendationapp.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {
    @Provides
    @Singleton
    fun getLocationProviderClient(context: Context) =
        LocationServices.getFusedLocationProviderClient(context)
}