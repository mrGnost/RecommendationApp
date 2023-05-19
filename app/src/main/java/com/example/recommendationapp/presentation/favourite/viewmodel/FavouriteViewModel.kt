package com.example.recommendationapp.presentation.favourite.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavouriteViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val localInteractor: LocalInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val favouriteLiveData = MutableLiveData<List<RestaurantShort>>()
    private val markedLiveData = MutableLiveData<List<RestaurantShort>>()
    private val accountLiveData = MutableLiveData<AccountLocal>()
    private val disposables = CompositeDisposable()

    fun clearLike(restaurant: RestaurantShort, account: AccountLocal?) {
        disposables.add(databaseInteractor.setLike(restaurant.id, false)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                if (account != null)
                    clearLikeOnAccount(restaurant, account)
                else
                    Log.d("FAVOURITE", "restaurant preference cleared")
            }, errorLiveData::setValue)
        )
    }

    private fun clearLikeOnAccount(restaurant: RestaurantShort, account: AccountLocal) {
        disposables.add(recommendationInteractor.removeFavourites(account.token, listOf(restaurant.id))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d("FAVOURITE", "restaurant preference cleared") }, errorLiveData::setValue)
        )
    }

    fun clearMark(restaurant: RestaurantShort, account: AccountLocal?) {
        disposables.add(databaseInteractor.setMark(restaurant.id, false)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                if (account != null)
                    clearMarkOnAccount(restaurant, account)
                else
                    Log.d("MARK", "restaurant preference cleared")
            }, errorLiveData::setValue)
        )
    }

    private fun clearMarkOnAccount(restaurant: RestaurantShort, account: AccountLocal) {
        disposables.add(recommendationInteractor.removeMarked(account.token, listOf(restaurant.id))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d("MARK", "restaurant preference cleared") }, errorLiveData::setValue)
        )
    }

    fun getFavouritesByIds(ids: List<Int>) {
        disposables.add(databaseInteractor.getRestaurantsByIds(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(favouriteLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun getMarkedByIds(ids: List<Int>) {
        disposables.add(databaseInteractor.getRestaurantsByIds(ids)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(markedLiveData::setValue, errorLiveData::setValue)
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

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getRestaurantIdsLiveData(favourite: Boolean): LiveData<List<Int>> {
        return databaseInteractor.getRestaurantIds(favourite)
    }

    fun getFavouriteLiveData(): LiveData<List<RestaurantShort>> {
        return favouriteLiveData
    }

    fun getMarkedLiveData(): LiveData<List<RestaurantShort>> {
        return markedLiveData
    }

    fun getAccountLiveData(): LiveData<AccountLocal> {
        return accountLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}