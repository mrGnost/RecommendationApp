package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.datastore.db.RestaurantsDao
import com.example.recommendationapp.data.model.RestaurantDataEntity
import com.example.recommendationapp.data.model.RestaurantShortDataEntity
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.DatabaseRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DatabaseRepositoryImpl
@Inject constructor(private val restaurantsDao: RestaurantsDao) : DatabaseRepository {
    override fun putRestaurants(restaurants: List<Restaurant>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putRestaurants(restaurants.map { RestaurantDataEntity.fromEntity(it) })
        }
    }

    override fun putRestaurantsShort(restaurants: List<RestaurantShort>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putRestaurantsShort(restaurants.map { RestaurantShortDataEntity.fromEntity(it) })
        }
    }

    override fun makeRecommended(restaurants: List<RestaurantShort>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateRestaurantsShort(restaurants.map {
                RestaurantShortDataEntity.fromEntity(it).apply { favourite = false }
            })
        }
    }

    override fun makeFavourite(restaurants: List<RestaurantShort>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateRestaurantsShort(restaurants.map {
                RestaurantShortDataEntity.fromEntity(it).apply { favourite = true }
            })
        }
    }

    override fun removeMark(restaurant: RestaurantShort): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateRestaurantShort(
                RestaurantShortDataEntity.fromEntity(restaurant).apply { favourite = null }
            )
        }
    }

    override fun getInArea(
        recommended: Boolean,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>> {
        if (recommended)
            return Single.fromCallable {
                restaurantsDao
                    .getRecommendedInArea(leftLat, leftLon, rightLat, rightLon)
                    .map { it.toEntity() }
            }
        return Single.fromCallable {
            restaurantsDao
                .getRestaurantsInArea(leftLat, leftLon, rightLat, rightLon)
                .map { it.toEntity() }
        }
    }
}