package com.example.recommendationapp.presentation.onboarding.search.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val localInteractor: LocalInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val restaurantsLiveData = MutableLiveData<List<RestaurantShort>>()
    private val accountLiveData = MutableLiveData<Account>()
    private val recommendationsSavedLiveData = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()

    fun clearLike(restaurant: RestaurantShort) {
        disposables.add(databaseInteractor.setLike(restaurant.id, false)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d("FAVOURITE", "restaurant preference cleared") }, errorLiveData::setValue)
        )
    }

    fun getRestaurantsByIds(ids: List<Int>) {
        disposables.add(databaseInteractor.getRestaurantsByIds(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(restaurantsLiveData::setValue, errorLiveData::setValue)
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

    fun cacheRecommendedIds(userId: Int) {
        disposables.add(recommendationInteractor.getRecommended(userId)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::saveRecommendedIdsToDb, errorLiveData::setValue)
        )
    }

    fun cacheRecommendedIds(liked: List<Int>) {
        disposables.add(recommendationInteractor.getRecommendedUnauthorized(liked)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::saveRecommendedIdsToDb, errorLiveData::setValue)
        )
    }

    private fun saveRecommendedIdsToDb(ids: List<Int>) {
        disposables.add(databaseInteractor.makeRecommended(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ recommendationsSavedLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getRestaurantIdsLiveData(): LiveData<List<Int>> {
        return databaseInteractor.getRestaurantIds(true)
    }

    fun getRestaurantsLiveData(): LiveData<List<RestaurantShort>> {
        return restaurantsLiveData
    }

    fun getAccountLiveData(): LiveData<Account> {
        return accountLiveData
    }

    fun getRecommendationSavedLiveData(): LiveData<Boolean> {
        return recommendationsSavedLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}