package com.example.recommendationapp.utils.callback

import com.example.recommendationapp.domain.model.RestaurantShort

interface RestaurantClickListener {
    fun onClick(restaurantShort: RestaurantShort, position: Int)
}