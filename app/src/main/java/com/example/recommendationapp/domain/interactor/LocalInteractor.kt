package com.example.recommendationapp.domain.interactor

import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.domain.repository.LocalRepository
import javax.inject.Inject

class LocalInteractor @Inject constructor(private val localRepository: LocalRepository) {
    fun setAccount(account: AccountLocal) = localRepository.setAccount(account)

    fun getAccount() = localRepository.getAccount()

    fun clearAccount() = localRepository.clearAccount()

    fun setOnboardingViewed() = localRepository.setOnboardingViewed()

    fun checkOnboardingViewed() = localRepository.checkOnboardingViewed()
}