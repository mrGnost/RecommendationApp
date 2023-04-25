package com.example.recommendationapp.data.repository

import com.example.recommendationapp.data.filter.FilterSource
import com.example.recommendationapp.domain.repository.FilterRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class FilterRepositoryImpl @Inject constructor(private val filterSource: FilterSource) : FilterRepository {
    override fun setRecommendationFilter(value: Boolean): Completable {
        return Completable.fromRunnable { filterSource.setRecommendationFilter(value) }
    }

    override fun getRecommendationFilter(): Single<Boolean> {
        return Single.fromCallable { filterSource.getRecommendationFilter() }
    }
}