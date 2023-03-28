package com.example.recommendationapp.presentation.map.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.Location
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.yandex.mapkit.map.VisibleRegion
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MapViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val restaurantsLiveData = MutableLiveData<List<RestaurantShort>>()
    private val recommendedCountLiveData = MutableLiveData<Int>()
    private val recommendedFilterLiveData = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()

    fun getRestaurantsInArea(recommended: Boolean, area: VisibleRegion) {
        val leftLat = minOf(area.topLeft.latitude, area.bottomLeft.latitude,
            area.bottomRight.latitude, area.topRight.latitude)
        val leftLon = minOf(area.topLeft.longitude, area.bottomLeft.longitude,
            area.bottomRight.longitude, area.topRight.longitude)
        val rightLat = maxOf(area.topLeft.latitude, area.bottomLeft.latitude,
            area.bottomRight.latitude, area.topRight.latitude)
        val rightLon = maxOf(area.topLeft.longitude, area.bottomLeft.longitude,
            area.bottomRight.longitude, area.topRight.longitude)
        disposables.add(
            databaseInteractor.getRestaurantsInArea(recommended, leftLat, leftLon, rightLat, rightLon)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressLiveData.postValue(true) }
                .doAfterTerminate { progressLiveData.postValue(false) }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe(restaurantsLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun getRecommendedCount() {
        disposables.add(
            databaseInteractor.getRecommendedCount()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressLiveData.postValue(true) }
                .doAfterTerminate { progressLiveData.postValue(false) }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe(recommendedCountLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun setFilterChecked(filter: Filter, checked: Boolean, filterId: Int) {
        disposables.add(databaseInteractor.setFilterChecked(filter, checked, filterId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { Log.d(DB, "filter ${filter.variants[filterId]} is now $checked") },
                errorLiveData::setValue
            )
        )
    }

    fun clearAllFilters(filters: List<Filter>) {
        disposables.add(databaseInteractor.clearFilters(filters)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { Log.d(DB, "filters cleared") },
                errorLiveData::setValue
            )
        )
    }

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getRestaurantsLiveData(): LiveData<List<RestaurantShort>> {
        return restaurantsLiveData
    }

    fun getRecommendedCountLiveData(): LiveData<Int> {
        return recommendedCountLiveData
    }

    fun getFiltersLiveData(): LiveData<List<Filter>> {
        return databaseInteractor.getFilters()
    }

    fun getFiltersCountLiveData(): LiveData<Int> {
        return databaseInteractor.getFiltersCount()
    }

    fun getRecommendedFilterLiveData(): LiveData<Boolean> {
        return recommendedFilterLiveData
    }

    fun setRecommendedFilterValue(value: Boolean) {
        recommendedFilterLiveData.value = value
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}