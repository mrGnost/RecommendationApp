package com.example.recommendationapp.di

import com.example.recommendationapp.data.filter.FilterSource
import com.example.recommendationapp.data.filter.FilterSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FilterModule {
    @Provides
    @Singleton
    fun getFilterSource() = FilterSourceImpl()
}