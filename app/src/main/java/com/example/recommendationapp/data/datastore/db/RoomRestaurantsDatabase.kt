package com.example.recommendationapp.data.datastore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recommendationapp.data.model.FilterDataEntity
import com.example.recommendationapp.data.model.RestaurantDataEntity
import com.example.recommendationapp.data.model.RestaurantShortDataEntity
import com.example.recommendationapp.data.model.SocialDataEntity

@Database(
    entities = [RestaurantShortDataEntity::class, FilterDataEntity::class],
    version = DatabaseScheme.DB_VERSION,
    exportSchema = true
)
abstract class RoomRestaurantsDatabase : RoomDatabase() {
    abstract fun restaurantsDao(): RestaurantsDao
}