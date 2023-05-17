package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.local.LocalDataSource
import com.example.recommendationapp.data.model.AccountDataEntity
import com.example.recommendationapp.data.model.AccountLocalDataEntity
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.domain.repository.LocalRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(private val dataSource: LocalDataSource) : LocalRepository {
    override fun setAccount(account: AccountLocal): Completable {
        return Completable.fromRunnable { dataSource.setAccount(AccountLocalDataEntity.fromEntity(account)) }
    }

    override fun getAccount(): Single<AccountLocal> {
        return Single.fromCallable { dataSource.getAccount().toEntity() }
    }

    override fun clearAccount(): Completable {
        return Completable.fromRunnable { dataSource.clearAccount() }
    }

    override fun setOnboardingViewed(): Completable {
        return Completable.fromRunnable { dataSource.setOnboardingViewed() }
    }

    override fun checkOnboardingViewed(): Single<Boolean> {
        return Single.fromCallable { dataSource.checkOnboardingViewed() }
    }
}