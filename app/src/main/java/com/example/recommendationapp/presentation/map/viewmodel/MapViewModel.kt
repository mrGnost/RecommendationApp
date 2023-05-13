package com.example.recommendationapp.presentation.map.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.recommendationapp.App
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.FilterInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
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
    private val filterInteractor: FilterInteractor,
    private val localInteractor: LocalInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val restaurantsLiveData = MutableLiveData<List<RestaurantShort>>()
    private val recommendedCountLiveData = MutableLiveData<Int>()
    private val recommendedFilterLiveData = MutableLiveData<Boolean>()
    private val filteredRestaurantsLiveData = MutableLiveData<List<RestaurantShort>>()
    private val recommendedIdsLiveData = MutableLiveData<List<Int>>()
    private val accountLiveData = MutableLiveData<Account>()
    private val disposables = CompositeDisposable()

    fun getRestaurantsInArea(recommendedIds: List<Int>, area: VisibleRegion) {
        val leftLat = minOf(area.topLeft.latitude, area.bottomLeft.latitude,
            area.bottomRight.latitude, area.topRight.latitude)
        val leftLon = minOf(area.topLeft.longitude, area.bottomLeft.longitude,
            area.bottomRight.longitude, area.topRight.longitude)
        val rightLat = maxOf(area.topLeft.latitude, area.bottomLeft.latitude,
            area.bottomRight.latitude, area.topRight.latitude)
        val rightLon = maxOf(area.topLeft.longitude, area.bottomLeft.longitude,
            area.bottomRight.longitude, area.topRight.longitude)
        disposables.add(
            databaseInteractor.getRestaurantsInArea(recommendedIds, leftLat, leftLon, rightLat, rightLon)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { progressLiveData.postValue(true) }
                .doAfterTerminate { progressLiveData.postValue(false) }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe(restaurantsLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun getRecommendedIds(userId: Int) {
        disposables.add(recommendationInteractor.getRecommended(userId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(recommendedIdsLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun getRecommendedIds(favourites: List<Int>) {
        disposables.add(recommendationInteractor.getRecommendedUnauthorized(favourites)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(recommendedIdsLiveData::setValue, errorLiveData::setValue)
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

    fun putRecommendedToDb(ids: List<Int>) {
        disposables.add(databaseInteractor.makeRecommended(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d(DB, "recommended saved to DB") }, errorLiveData::setValue)
        )
    }

    fun getAccount() {
        disposables.add(localInteractor.getAccount()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(accountLiveData::setValue, errorLiveData::setValue)
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

    fun setRecommendedFilter(value: Boolean) {
        disposables.add(filterInteractor.setRecommendationFilter(value)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                { Log.d(FILTER, "recommended filter = $value") },
                errorLiveData::setValue
            )
        )
    }

    fun getRecommendedFilter() {
        disposables.add(filterInteractor.getRecommendationFilter()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                recommendedFilterLiveData::setValue,
                errorLiveData::setValue
            )
        )
    }

    fun getFilteredRestaurants(userId: Int, filters: List<Filter>, recommended: Boolean) {
        disposables.add(recommendationInteractor.getFilteredPlaces(userId, filters, recommended)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                this::getRestaurantsByIds,
                errorLiveData::setValue
            )
        )
    }

    private fun getRestaurantsByIds(ids: List<Int>) {
        disposables.add(databaseInteractor.getRestaurantsByIds(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(
                filteredRestaurantsLiveData::setValue,
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

    fun getRecommendedIdsLiveData(): LiveData<List<Int>> {
        return recommendedIdsLiveData
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

    fun getFilteredLiveData(): LiveData<List<RestaurantShort>> {
        return filteredRestaurantsLiveData
    }

    fun getFavouriteIdsLiveData(): LiveData<List<Int>> {
        return databaseInteractor.getRestaurantIds(true)
    }

    fun getAccountLiveData(): LiveData<Account> {
        return accountLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
        private const val FILTER = "FILTER_SOURCE"
    }
}