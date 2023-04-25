package com.example.recommendationapp.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface FilterRepository {
    fun setRecommendationFilter(value: Boolean): Completable

    fun getRecommendationFilter(): Single<Boolean>
}