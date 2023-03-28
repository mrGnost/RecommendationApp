package com.example.recommendationapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.recommendationapp.data.datastore.db.RestaurantsDao
import com.example.recommendationapp.data.model.FilterDataEntity
import com.example.recommendationapp.data.model.RestaurantDataEntity
import com.example.recommendationapp.data.model.RestaurantShortDataEntity
import com.example.recommendationapp.domain.model.Filter
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
                RestaurantShortDataEntity.fromEntity(it).apply { recommended = true }
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

    override fun setLike(restaurant: RestaurantShort, check: Boolean): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateRestaurantShort(
                RestaurantShortDataEntity.fromEntity(restaurant).apply { favourite = check }
            )
        }
    }

    override fun setMark(restaurant: RestaurantShort, check: Boolean): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateRestaurantShort(
                RestaurantShortDataEntity.fromEntity(restaurant).apply { marked = check }
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

    override fun getRestaurants(favourite: Boolean): LiveData<List<RestaurantShort>> {
        if (favourite)
            return restaurantsDao.getFavouriteRestaurants().map { x -> x.map { it.toEntity() } }
        return restaurantsDao.getMarkedRestaurants().map { x -> x.map { it.toEntity() } }
    }

    override fun findRestaurants(prefix: String): Single<List<RestaurantShort>> {
        return Single.fromCallable {
            restaurantsDao.findRestaurants(prefix).map { it.toEntity() }
        }
    }

    override fun putFilters(filters: List<Filter>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putFilters(filters.map { FilterDataEntity.fromEntity(it) })
        }
    }

    override fun getFilters(): LiveData<List<Filter>> {
        return restaurantsDao.getFilters().map { x -> x.map { it.toEntity() } }
    }

    override fun changeCheckedFilter(filter: Filter, value: Boolean, filterId: Int): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateFilter(FilterDataEntity.fromEntity(filter).apply {
                checked[filterId] = value
            })
        }
    }

    override fun getRecommendedCount(): Single<Int> {
        return Single.fromCallable {
            restaurantsDao.getRecommendedCount()
        }
    }

    override fun changeFavourite(id: Int, favourite: Boolean): Completable {
        return Completable.fromRunnable {
            restaurantsDao.changeFavouriteById(id, if (favourite) 1 else 0)
        }
    }

    override fun changeMark(id: Int, marked: Boolean): Completable {
        return Completable.fromRunnable {
            restaurantsDao.changeMarkById(id, if (marked) 1 else 0)
        }
    }
}