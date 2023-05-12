package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.Account
import io.reactivex.Completable
import io.reactivex.Single

interface LocalRepository {
    fun setAccount(account: Account): Completable

    fun getAccount(): Single<Account>

    fun setOnboardingViewed(): Completable

    fun checkOnboardingViewed(): Single<Boolean>
}