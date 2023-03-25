package com.example.recommendationapp.data.datastore.db

import androidx.room.*
import com.example.recommendationapp.data.model.RestaurantDataEntity
import com.example.recommendationapp.data.model.RestaurantShortDataEntity

@Dao
interface RestaurantsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRestaurantsShort(restaurants: List<RestaurantShortDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRestaurants(restaurants: List<RestaurantDataEntity>)

    @Update
    fun updateRestaurantsShort(restaurants: List<RestaurantShortDataEntity>)

    @Update
    fun updateRestaurantShort(restaurants: RestaurantShortDataEntity)

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE (latitude BETWEEN :leftLat AND :rightLat) " +
            "AND (longitude BETWEEN :leftLon AND :rightLon)")
    fun getRestaurantsInArea(leftLat: Double, leftLon: Double, rightLat: Double, rightLon: Double):
            List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE favourite = 0 AND (latitude BETWEEN :leftLat AND :rightLat) " +
        "AND (longitude BETWEEN :leftLon AND :rightLon)")
    fun getRecommendedInArea(leftLat: Double, leftLon: Double, rightLat: Double, rightLon: Double):
            List<RestaurantShortDataEntity>
}