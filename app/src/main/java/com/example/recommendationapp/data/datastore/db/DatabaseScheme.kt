package com.example.recommendationapp.data.datastore.db

object DatabaseScheme {
    const val DB_NAME = "restaurants"
    const val DB_VERSION = 6

    object RestaurantsTableScheme {
        const val SHORT_INFO_TABLE_NAME = "restaurants_short"
        const val FILTERS_TABLE_NAME = "filters"
        const val FAVOURITE_IDS = "favourite_ids"
        const val MARKED_IDS = "marked_ids"
        const val RECOMMENDED_IDS = "recommended_ids"
    }
}