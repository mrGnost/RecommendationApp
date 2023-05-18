package com.example.recommendationapp.presentation.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(
    private val recommendationInteractor: RecommendationInteractor,
    private val localInteractor: LocalInteractor,
    private val databaseInteractor: DatabaseInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val successLiveData = MutableLiveData<String>()
    private val accountLiveData = MutableLiveData<AccountLocal>()
    private val registeredLiveData = MutableLiveData<Boolean>()
    private val likesSavedLiveData = MutableLiveData<Boolean>()
    private val marksSavedLiveData = MutableLiveData<Boolean>()
    private val accountClearedLiveData = MutableLiveData<Boolean>()
    private val likesClearedLiveData = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()

    fun register(email: String, password: String) {
        disposables.add(recommendationInteractor.register(Account(email, password))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ registeredLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun login(email: String, password: String) {
        val account = Account(email, password)
        disposables.add(recommendationInteractor.login(account)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ saveAccount(AccountLocal(email, it.token)) }, errorLiveData::setValue)
        )
    }

    private fun saveAccount(account: AccountLocal) {
        disposables.add(localInteractor.setAccount(account)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ successLiveData.postValue(account.token) }, errorLiveData::setValue)
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

    fun sendLikesToAccount(token: String, likeIds: List<Int>) {
        disposables.add(recommendationInteractor.addFavourites(
            token,
            likeIds
        )
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ getLikesFromAccount(token) }, errorLiveData::setValue)
        )
    }

    private fun getLikesFromAccount(token: String) {
        disposables.add(recommendationInteractor.getFavourite(token)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::saveLikesLocally, errorLiveData::setValue)
        )
    }

    private fun saveLikesLocally(places: List<RestaurantShort>) {
        disposables.add(databaseInteractor.makeFavourite(places.map { it.id })
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ likesSavedLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun sendMarksToAccount(token: String, markIds: List<Int>) {
        disposables.add(recommendationInteractor.addMarked(
            token,
            markIds
        )
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ getMarksFromAccount(token) }, errorLiveData::setValue)
        )
    }

    private fun getMarksFromAccount(token: String) {
        disposables.add(recommendationInteractor.getMarked(token)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(this::saveMarksLocally, errorLiveData::setValue)
        )
    }

    private fun saveMarksLocally(places: List<RestaurantShort>) {
        disposables.add(databaseInteractor.makeMarked(places.map { it.id })
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ marksSavedLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun clearCache() {
        disposables.add(localInteractor.clearAccount()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ marksSavedLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun clearLikesAndMarks() {
        disposables.add(databaseInteractor.clearFavouritesAndMarked()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ marksSavedLiveData.postValue(true) }, errorLiveData::setValue)
        )
    }

    fun getErrorLiveData(): LiveData<Throwable> {
        return errorLiveData
    }

    fun getProgressLiveData(): LiveData<Boolean> {
        return progressLiveData
    }

    fun getSuccessLiveData(): LiveData<String> {
        return successLiveData
    }

    fun getAccountLiveData(): LiveData<AccountLocal> {
        return accountLiveData
    }

    fun getRegisteredLiveData(): LiveData<Boolean> {
        return registeredLiveData
    }

    fun getLikesSavedLiveData(): LiveData<Boolean> {
        return likesSavedLiveData
    }

    fun getMarksSavedLiveData(): LiveData<Boolean> {
        return marksSavedLiveData
    }

    fun getAccountClearedLiveData(): MutableLiveData<Boolean> {
        return accountClearedLiveData
    }

    fun getLikesClearedLiveData(): MutableLiveData<Boolean> {
        return likesClearedLiveData
    }

    fun getRestaurantIdsLiveData(favourite: Boolean): LiveData<List<Int>> {
        return databaseInteractor.getRestaurantIds(favourite)
    }

    companion object {
        private const val RETROFIT = "RETROFIT"
        private const val DB = "DATABASE_CUSTOM"
    }
}