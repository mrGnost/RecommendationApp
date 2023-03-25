package com.example.recommendationapp.di

import android.content.Context
import androidx.room.Room
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.example.recommendationapp.data.datastore.db.RestaurantsDao
import com.example.recommendationapp.data.datastore.db.RoomRestaurantsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    fun provideRoomDatabase(context: Context): RoomRestaurantsDatabase = Room.databaseBuilder(
        context,
        RoomRestaurantsDatabase::class.java,
        DatabaseScheme.DB_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideWordsDao(db: RoomRestaurantsDatabase): RestaurantsDao = db.restaurantsDao()
}