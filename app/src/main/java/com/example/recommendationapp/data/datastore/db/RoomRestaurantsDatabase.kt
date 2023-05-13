package com.example.recommendationapp.data.datastore.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recommendationapp.data.model.*

@Database(
    entities = [
        RestaurantShortDataEntity::class,
        FilterDataEntity::class,
        RecommendedID::class,
        FavouriteID::class,
        MarkedID::class
    ],
    version = DatabaseScheme.DB_VERSION,
    exportSchema = true
)
abstract class RoomRestaurantsDatabase : RoomDatabase() {
    abstract fun restaurantsDao(): RestaurantsDao
}