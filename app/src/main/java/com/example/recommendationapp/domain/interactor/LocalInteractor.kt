package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.repository.LocalRepository
import javax.inject.Inject

class LocalInteractor @Inject constructor(private val localRepository: LocalRepository) {
    fun setAccount(account: Account) = localRepository.setAccount(account)

    fun getAccount() = localRepository.getAccount()

    fun setOnboardingViewed() = localRepository.setOnboardingViewed()

    fun checkOnboardingViewed() = localRepository.checkOnboardingViewed()
}