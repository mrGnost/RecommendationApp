package com.example.recommendationapp.presentation.onboarding.finish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FinishViewModel(
    private val databaseInteractor: DatabaseInteractor,
    private val localInteractor: LocalInteractor,
    private val schedulers: SchedulerProvider
) : ViewModel() {
    private val progressLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<Throwable>()
    private val recommendedCountLiveData = MutableLiveData<Int>()
    private val disposables = CompositeDisposable()

    fun setOnboardingFinished() {
        disposables.add(localInteractor.setOnboardingViewed()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({ Log.d(DB, "onboarding viewed set") }, errorLiveData::setValue)
        )
    }

    fun getRecommendedCount() {
        disposables.add(databaseInteractor.getRecommendedCount()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { progressLiveData.postValue(true) }
            .doAfterTerminate { progressLiveData.postValue(false) }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe(recommendedCountLiveData::setValue, errorLiveData::setValue)
        )
    }

    fun getRecommendedCountLiveData(): MutableLiveData<Int> {
        return recommendedCountLiveData
    }

    companion object {
        private const val DB = "DATABASE_CUSTOM"
        private const val LOCAL = "LOCAL_SOURCE"
    }
}