package com.example.recommendationapp.di

import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MapKitModule {
    @Provides
    @Singleton
    fun provideSearchManager(): SearchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.ONLINE
    )
}