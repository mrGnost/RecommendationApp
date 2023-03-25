package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllRestaurantsResponse(
    val cafes: List<RestaurantShort>,
    val recommendedCafes: List<Int>,
    val favoriteCafes: List<Int>
) : Parcelable
