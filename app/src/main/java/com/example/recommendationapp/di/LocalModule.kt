package com.example.recommendationapp.di

import android.content.Context
import com.example.recommendationapp.data.local.LocalDataSourceImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {
    @Provides
    @Singleton
    fun getLocalDataSource(context: Context) = LocalDataSourceImpl(context)
}