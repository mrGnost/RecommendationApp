package com.example.recommendationapp.presentation.splash.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AllRestaurantsResponse
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val localInteractor: LocalInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val completeLiveData = MutableLiveData<Boolean>()
    private val recommendationsCompleteLiveData = MutableLiveData<Boolean>()
    private val favouritesCompleteLiveData = MutableLiveData<Boolean>()
    private val filtersCompleteLiveData = MutableLiveData<Boolean>()
    private val onboardingFinishedLiveData = MutableLiveData<Boolean>()
    private val accountLiveData = MutableLiveData<Account>()
    private val disposables = CompositeDisposable()

    fun cacheAllRestaurants() {
        disposables.add(recommendationInteractor.getAllRestaurants()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::putRestaurantsToDb, errorLiveData::setValue)
        )
    }

    private fun putRestaurantsToDb(restaurants: List<RestaurantShort>) {
        disposables.add(databaseInteractor.putRestaurantsShort(restaurants)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ completeLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun getOnboardingFinished() {
        disposables.add(localInteractor.checkOnboardingViewed()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(onboardingFinishedLiveData::setValue, errorLiveData::setValue)
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

    fun getRecommendations(userId: Int) {
        disposables.add(recommendationInteractor.getRecommended(userId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::putRecommendationsToDb, errorLiveData::setValue)
        )
    }

    private fun putRecommendationsToDb(restaurantIds: List<Int>) {
        disposables.add(databaseInteractor.makeRecommended(restaurantIds)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ recommendationsCompleteLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun getFavourites(userId: Int) {
        disposables.add(recommendationInteractor.getFavourite(userId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::putFavouritesToDb, errorLiveData::setValue)
        )
    }

    private fun putFavouritesToDb(restaurants: List<RestaurantShort>) {
        disposables.add(databaseInteractor.makeFavourite(restaurants.map { it.id })
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ favouritesCompleteLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun getFilters() {
        disposables.add(recommendationInteractor.getFilters()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::putFiltersToDb, errorLiveData::setValue)
        )
    }

    private fun putFiltersToDb(filters: List<Filter>) {
        disposables.add(databaseInteractor.putFilters(filters)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ filtersCompleteLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
        disposables.clear()
    }

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getCompleteLiveData(): LiveData<Boolean> {
        return completeLiveData
    }

    fun getOnboardingFinishedLiveData(): LiveData<Boolean> {
        return onboardingFinishedLiveData
    }

    fun getAccountLiveData(): LiveData<Account> {
        return accountLiveData
    }

    fun getRecommendationsCompleteLiveData(): LiveData<Boolean> {
        return recommendationsCompleteLiveData
    }

    fun getFavouritesCompleteLiveData(): LiveData<Boolean> {
        return favouritesCompleteLiveData
    }

    fun getFiltersCompleteLiveData(): LiveData<Boolean> {
        return filtersCompleteLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}