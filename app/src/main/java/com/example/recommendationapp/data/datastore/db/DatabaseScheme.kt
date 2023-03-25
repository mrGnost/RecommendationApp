package com.example.recommendationapp.data.datastore.db

object DatabaseScheme {
    const val DB_NAME = "restaurants"
    const val DB_VERSION = 2

    object RestaurantsTableScheme {
        const val SHORT_INFO_TABLE_NAME = "restaurants_short"
        const val FULL_INFO_TABLE_NAME = "restaurants_full"
    }
}