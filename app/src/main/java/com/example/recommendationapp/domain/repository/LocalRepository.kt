package com.example.recommendationapp.domain.repository

import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AccountLocal
import io.reactivex.Completable
import io.reactivex.Single

interface LocalRepository {
    fun setAccount(account: AccountLocal): Completable

    fun getAccount(): Single<AccountLocal>

    fun clearAccount(): Completable

    fun setOnboardingViewed(): Completable

    fun checkOnboardingViewed(): Single<Boolean>
}