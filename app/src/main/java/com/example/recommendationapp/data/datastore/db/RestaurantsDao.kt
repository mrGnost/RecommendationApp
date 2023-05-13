package com.example.recommendationapp.data.datastore.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.recommendationapp.data.model.*

@Dao
interface RestaurantsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRestaurantsShort(restaurants: List<RestaurantShortDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putFilters(filters: List<FilterDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRecommendedIds(ids: List<RecommendedID>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putFavouriteIds(ids: List<FavouriteID>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putMarkedIds(ids: List<MarkedID>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putRecommendedId(id: RecommendedID)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putFavouriteId(id: FavouriteID)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putMarkedId(id: MarkedID)

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
            "WHERE id IN (:ids) AND (latitude BETWEEN :leftLat AND :rightLat) " +
        "AND (longitude BETWEEN :leftLon AND :rightLon)")
    fun getInAreaByIds(leftLat: Double, leftLon: Double, rightLat: Double, rightLon: Double, id: List<Int>):
            List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.FAVOURITE_IDS}")
    fun getFavouriteIds(): LiveData<List<FavouriteID>>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.MARKED_IDS}")
    fun getMarkedIds(): LiveData<List<MarkedID>>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME} " +
            "WHERE name LIKE :prefix || '%'")
    fun findRestaurants(prefix: String): List<RestaurantShortDataEntity>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.FILTERS_TABLE_NAME}")
    fun getFilters(): LiveData<List<FilterDataEntity>>

    @Query("SELECT COUNT(id) FROM ${DatabaseScheme.RestaurantsTableScheme.RECOMMENDED_IDS}")
    fun getRecommendedCount(): Int

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.RECOMMENDED_IDS} WHERE id = :id")
    fun checkIfRecommended(id: Int): List<RecommendedID>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.FAVOURITE_IDS} WHERE id = :id")
    fun checkIfFavourite(id: Int): List<FavouriteID>

    @Query("SELECT * FROM ${DatabaseScheme.RestaurantsTableScheme.MARKED_IDS} WHERE id = :id")
    fun checkIfMarked(id: Int): List<MarkedID>

    @Query("DELETE FROM ${DatabaseScheme.RestaurantsTableScheme.RECOMMENDED_IDS} WHERE id = :id")
    fun removeRecommendedId(id: Int)

    @Query("DELETE FROM ${DatabaseScheme.RestaurantsTableScheme.RECOMMENDED_IDS}")
    fun removeAllRecommended()

    @Query("DELETE FROM ${DatabaseScheme.RestaurantsTableScheme.FAVOURITE_IDS} WHERE id = :id")
    fun removeFavouriteId(id: Int)

    @Query("DELETE FROM ${DatabaseScheme.RestaurantsTableScheme.MARKED_IDS} WHERE id = :id")
    fun removeMarkedId(id: Int)
}