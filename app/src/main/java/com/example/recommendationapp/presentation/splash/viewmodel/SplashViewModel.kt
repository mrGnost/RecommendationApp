package com.example.recommendationapp.presentation.splash.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.AllRestaurantsResponse
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SplashViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val completeLiveData = MutableLiveData<Boolean>()
    private val recommendationsCompleteLiveData = MutableLiveData<Boolean>()
    private val favouritesCompleteLiveData = MutableLiveData<Boolean>()
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

    private fun putRecommendationsToDb(restaurants: List<RestaurantShort>) {
        disposables.add(databaseInteractor.makeRecommended(restaurants)
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
        disposables.add(databaseInteractor.makeFavourite(restaurants)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ favouritesCompleteLiveData.postValue(true) }, errorLiveData::setValue)
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

    fun getRecommendationsCompleteLiveData(): LiveData<Boolean> {
        return recommendationsCompleteLiveData
    }

    fun getFavouritesCompleteLiveData(): LiveData<Boolean> {
        return favouritesCompleteLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}