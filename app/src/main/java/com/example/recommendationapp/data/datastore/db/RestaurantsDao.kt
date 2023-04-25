package com.example.recommendationapp.data.datastore.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.recommendationapp.data.model.FilterDataEntity
import com.example.recommendationapp.data.model.RestaurantDataEntity
import com.example.recommendationapp.data.model.RestaurantShortDataEntity
import io.reactivex.Completable

@Dao
interface RestaurantsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRestaurantsShort(restaurants: List<RestaurantShortDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRestaurants(restaurants: List<RestaurantDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putFilters(filters: List<FilterDataEntity>)

    @Update
    fun updateRestaurantsShort(restaurants: List<RestaurantShortDataEntity>)

    @Update
    fun updateRestaurantShort(restaurant: RestaurantShortDataEntity)

    @Update
    fun updateFilter(filter: FilterDataEntity)

    @Update
    fun updateFilters(checked: List<FilterDataEntity>)

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE (latitude BETWEEN :leftLat AND :rightLat) " +
            "AND (longitude BETWEEN :leftLon AND :rightLon)")
    fun getRestaurantsInArea(leftLat: Double, leftLon: Double, rightLat: Double, rightLon: Double):
            List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE id IN (:ids)")
    fun getRestaurantsByIds(ids: List<Int>): List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE recommended = 1 AND (latitude BETWEEN :leftLat AND :rightLat) " +
        "AND (longitude BETWEEN :leftLon AND :rightLon)")
    fun getRecommendedInArea(leftLat: Double, leftLon: Double, rightLat: Double, rightLon: Double):
            List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE favourite = 1")
    fun getFavouriteRestaurants(): LiveData<List<RestaurantShortDataEntity>>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE marked = 1")
    fun getMarkedRestaurants(): LiveData<List<RestaurantShortDataEntity>>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.FULL_INFO_TABLE_NAME} " +
            "WHERE id = :id")
    fun getRestaurantInfo(id: Int): RestaurantDataEntity

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE name LIKE :prefix || '%'")
    fun findRestaurants(prefix: String): List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.FILTERS_TABLE_NAME}")
    fun getFilters(): LiveData<List<FilterDataEntity>>

    @Query("SELECT COUNT(id) FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE recommended = 1")
    fun getRecommendedCount(): Int

    @Query("UPDATE ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "SET favourite = :favourite WHERE id = :id")
    fun changeFavouriteById(id: Int, favourite: Int)

    @Query("UPDATE ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "SET marked = :marked WHERE id = :id")
    fun changeMarkById(id: Int, marked: Int)
}