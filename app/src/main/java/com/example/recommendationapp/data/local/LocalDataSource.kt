package com.example.recommendationapp.data.local

import com.example.recommendationapp.data.model.AccountDataEntity

interface LocalDataSource {
    fun setAccount(account: AccountDataEntity)

    fun getAccount(): AccountDataEntity

    fun setOnboardingViewed()

    fun checkOnboardingViewed(): Boolean
}