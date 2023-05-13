package com.example.recommendationapp.presentation.restaurant.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RestaurantViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val restaurantLiveData = MutableLiveData<Restaurant>()
    private val similarLiveData = MutableLiveData<List<RestaurantShort>>()
    private val isRecommendedLiveData = MutableLiveData<Boolean>()
    private val isFavouriteLiveData = MutableLiveData<Boolean>()
    private val isMarkedLiveData = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()

    fun getRestaurantInfo(id: Int) {
        disposables.add(recommendationInteractor.getRestaurant(id)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(restaurantLiveData::setValue, errorLiveData::setValue))
    }

    fun setFavourite(id: Int, favourite: Boolean) {
        disposables.add(databaseInteractor.setLike(id, favourite)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d(DB, "restaurant with id=$id fav changed") }, errorLiveData::setValue))
    }

    fun setMark(id: Int, marked: Boolean) {
        disposables.add(databaseInteractor.setMark(id, marked)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d(DB, "restaurant with id=$id mark changed") }, errorLiveData::setValue))
    }

    fun getSimilar(id: Int, amount: Int) {
        disposables.add(recommendationInteractor.getSimilar(id, amount)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(similarLiveData::setValue, errorLiveData::setValue))
    }

    fun checkIfRecommended(id: Int) {
        disposables.add(databaseInteractor.checkIfRecommended(id)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(isRecommendedLiveData::setValue, errorLiveData::setValue))
    }

    fun checkIfFavourite(id: Int) {
        disposables.add(databaseInteractor.checkIfFavourite(id)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(isFavouriteLiveData::setValue, errorLiveData::setValue))
    }

    fun checkIfMarked(id: Int) {
        disposables.add(databaseInteractor.checkIfMarked(id)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(isMarkedLiveData::setValue, errorLiveData::setValue))
    }

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getRestaurantLiveData(): LiveData<Restaurant> {
        return restaurantLiveData
    }

    fun getSimilarLiveData(): LiveData<List<RestaurantShort>> {
        return similarLiveData
    }

    fun getIsRecommendedLiveData(): LiveData<Boolean> {
        return isRecommendedLiveData
    }

    fun getIsFavouriteLiveData(): LiveData<Boolean> {
        return isFavouriteLiveData
    }

    fun getIsMarkedLiveData(): LiveData<Boolean> {
        return isMarkedLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }

}