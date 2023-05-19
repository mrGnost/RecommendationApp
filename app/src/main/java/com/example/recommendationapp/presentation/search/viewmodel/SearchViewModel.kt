package com.example.recommendationapp.presentation.search.viewmodel

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
import io.reactivex.Completable
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
    private val resultLiveData = MutableLiveData<List<RestaurantShort>>()
    private val accountLiveData = MutableLiveData<AccountLocal>()
    private val disposables = CompositeDisposable()

    fun changeLike(restaurant: RestaurantShort, value: Boolean, account: AccountLocal?) {
        disposables.add(databaseInteractor.setLike(restaurant.id, value)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                if (account != null)
                    setFavouriteToAccount(
                        restaurant.id,
                        account,
                        if (value)
                            recommendationInteractor::addFavourites
                        else
                            recommendationInteractor::removeFavourites
                    )
                else
                    Log.d("FAVOURITE", "restaurant preference cleared")
            }, errorLiveData::setValue))
    }

    private fun setFavouriteToAccount(
        id: Int,
        account: AccountLocal,
        method: (String, List<Int>) -> Completable
    ) {
        disposables.add(method(account.token, listOf(id))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d("FAVOURITE", "restaurant preference cleared") }, errorLiveData::setValue)
        )
    }

    fun search(prefix: String) {
        disposables.add(databaseInteractor.findRestaurants(prefix)
            .observeOn(schedulers.io()).subscribeOn(schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribe(resultLiveData::postValue, errorLiveData::postValue))
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

    fun getResultLiveData(): LiveData<List<RestaurantShort>> {
        return resultLiveData
    }

    fun getFavouriteIdsLiveData(): LiveData<List<Int>> {
        return databaseInteractor.getRestaurantIds(true)
    }

    fun getAccountLiveData(): LiveData<AccountLocal> {
        return accountLiveData
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}