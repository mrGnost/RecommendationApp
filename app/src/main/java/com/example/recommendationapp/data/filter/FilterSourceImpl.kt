package com.example.recommendationapp.data.filter

class FilterSourceImpl : FilterSource {
    var recommedation = true

    override fun setRecommendationFilter(value: Boolean) {
        recommedation = value
    }

    override fun getRecommendationFilter() = recommedation
}