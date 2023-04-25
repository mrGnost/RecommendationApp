package com.example.recommendationapp.data.filter

import androidx.lifecycle.LiveData

interface FilterSource {
    fun setRecommendationFilter(value: Boolean)

    fun getRecommendationFilter(): Boolean
}