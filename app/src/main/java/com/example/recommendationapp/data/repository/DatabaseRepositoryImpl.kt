package com.example.recommendationapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.recommendationapp.data.datastore.db.RestaurantsDao
import com.example.recommendationapp.data.model.*
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.domain.repository.DatabaseRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class DatabaseRepositoryImpl
@Inject constructor(private val restaurantsDao: RestaurantsDao) : DatabaseRepository {

    override fun putRestaurantsShort(restaurants: List<RestaurantShort>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putRestaurantsShort(restaurants.map { RestaurantShortDataEntity.fromEntity(it) })
        }
    }

    override fun makeRecommended(ids: List<Int>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putRecommendedIds(ids.map {
                RecommendedID.fromEntity(it)
            })
        }
    }

    override fun makeFavourite(ids: List<Int>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.putFavouriteIds(ids.map {
                FavouriteID.fromEntity(it)
            })
        }
    }

    override fun setLike(id: Int, check: Boolean): Completable {
        return Completable.fromRunnable {
            if (check)
                restaurantsDao.putFavouriteId(
                    FavouriteID.fromEntity(id)
                )
            else
                restaurantsDao.removeFavouriteId(id)
        }
    }

    override fun setMark(id: Int, check: Boolean): Completable {
        return Completable.fromRunnable {
            if (check)
                restaurantsDao.putMarkedId(
                    MarkedID.fromEntity(id)
                )
            else
                restaurantsDao.removeMarkedId(id)
        }
    }

    override fun getInArea(
        recommendedIds: List<Int>,
        leftLat: Double,
        leftLon: Double,
        rightLat: Double,
        rightLon: Double
    ): Single<List<RestaurantShort>> {
        if (recommendedIds.isNotEmpty())
            return Single.fromCallable {
                restaurantsDao
                    .getInAreaByIds(leftLat, leftLon, rightLat, rightLon, recommendedIds)
                    .map { it.toEntity() }
            }
        return Single.fromCallable {
            restaurantsDao
                .getRestaurantsInArea(leftLat, leftLon, rightLat, rightLon)
                .map { it.toEntity() }
        }
    }

    override fun getRestaurantIds(favourite: Boolean): LiveData<List<Int>> {
        if (favourite)
            return restaurantsDao.getFavouriteIds().map { x -> x.map { it.toEntity() } }
        return restaurantsDao.getMarkedIds().map { x -> x.map { it.toEntity() } }
    }

    override fun getRestaurantsByIds(ids: List<Int>): Single<List<RestaurantShort>> {
        return Single.fromCallable {
            restaurantsDao.getRestaurantsByIds(ids).map { it.toEntity() }
        }
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

    override fun clearFilters(filters: List<Filter>): Completable {
        return Completable.fromRunnable {
            restaurantsDao.updateFilters(filters.map {
                val filter = FilterDataEntity.fromEntity(it)
                filter.checked.replaceAll { false }
                filter
            })
        }
    }

    override fun changeCheckedFilter(filter: Filter, value: Boolean, filterId: Int): Completable {
        return Completable.fromRunnable {
            val newFilter = FilterDataEntity.fromEntity(filter)
            newFilter.checked[filterId] = value
            restaurantsDao.updateFilter(newFilter)
        }
    }

    override fun getRecommendedCount(): Single<Int> {
        return Single.fromCallable {
            restaurantsDao.getRecommendedCount()
        }
    }

    override fun getFiltersCount(): LiveData<Int> {
        return restaurantsDao.getFilters().map {x ->
            x.sumOf { y -> y.checked.count { it } }
        }
    }
}