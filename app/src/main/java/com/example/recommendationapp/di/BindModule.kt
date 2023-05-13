package com.example.recommendationapp.di

import androidx.room.RoomDatabase
import com.example.recommendationapp.data.datastore.db.RoomRestaurantsDatabase
import com.example.recommendationapp.data.filter.FilterSource
import com.example.recommendationapp.data.filter.FilterSourceImpl
import com.example.recommendationapp.data.local.LocalDataSource
import com.example.recommendationapp.data.local.LocalDataSourceImpl
import com.example.recommendationapp.data.repository.DatabaseRepositoryImpl
import com.example.recommendationapp.data.repository.FilterRepositoryImpl
import com.example.recommendationapp.data.repository.LocalRepositoryImpl
import com.example.recommendationapp.data.repository.RecommendationRepositoryImpl
import com.example.recommendationapp.domain.repository.DatabaseRepository
import com.example.recommendationapp.domain.repository.FilterRepository
import com.example.recommendationapp.domain.repository.LocalRepository
import com.example.recommendationapp.domain.repository.RecommendationRepository
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.example.recommendationapp.utils.scheduler.SchedulerProviderImpl
import dagger.Binds
import dagger.Module

@Module
interface BindModule {
    @Binds
    fun bindRecommendationRepository(impl: RecommendationRepositoryImpl): RecommendationRepository

    @Binds
    fun bindDatabaseRepository(impl: DatabaseRepositoryImpl): DatabaseRepository

    @Binds
    fun bindFilterRepository(impl: FilterRepositoryImpl): FilterRepository

    @Binds
    fun bindLocalRepository(impl: LocalRepositoryImpl): LocalRepository

    @Binds
    fun bindRoomDatabase(impl: RoomRestaurantsDatabase): RoomDatabase

    @Binds
    fun bindSchedulers(impl: SchedulerProviderImpl): SchedulerProvider

    @Binds
    fun bindFilterSource(impl: FilterSourceImpl): FilterSource

    @Binds
    fun bindLocalSource(impl: LocalDataSourceImpl): LocalDataSource
}