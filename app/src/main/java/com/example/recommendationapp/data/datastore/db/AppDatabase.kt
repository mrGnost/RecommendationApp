package com.example.recommendationapp.data.datastore.db

import android.content.Context
import androidx.room.Room

object AppDatabase {
    private var database: RoomRestaurantsDatabase? = null

    fun getDatabase(context: Context): RoomRestaurantsDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                RoomRestaurantsDatabase::class.java,
                DatabaseScheme.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return database!!
    }
}