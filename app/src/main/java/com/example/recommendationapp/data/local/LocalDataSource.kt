package com.example.recommendationapp.data.local

import com.example.recommendationapp.data.model.AccountLocalDataEntity

interface LocalDataSource {
    fun setAccount(account: AccountLocalDataEntity)

    fun getAccount(): AccountLocalDataEntity

    fun clearAccount()

    fun setOnboardingViewed()

    fun checkOnboardingViewed(): Boolean
}