package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.local.LocalDataSource
import com.example.recommendationapp.data.model.AccountDataEntity
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.repository.LocalRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(private val dataSource: LocalDataSource) : LocalRepository {
    override fun setAccount(account: Account): Completable {
        return Completable.fromRunnable { dataSource.setAccount(AccountDataEntity.fromEntity(account)) }
    }

    override fun getAccount(): Single<Account> {
        return Single.fromCallable { dataSource.getAccount().toEntity() }
    }

    override fun setOnboardingViewed(): Completable {
        return Completable.fromRunnable { dataSource.setOnboardingViewed() }
    }

    override fun checkOnboardingViewed(): Single<Boolean> {
        return Single.fromCallable { dataSource.checkOnboardingViewed() }
    }
}