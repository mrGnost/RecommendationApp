package com.example.recommendationapp.data.model

import android.os.Parcelable
import com.example.recommendationapp.domain.model.AllRestaurantsResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllRestaurantsResponseDataEntity(
    val cafes: List<RestaurantShortDataEntity>,
    val recommendedCafes: List<Int>,
    val favoriteCafes: List<Int>
) : Parcelable {
    constructor() : this(listOf(), listOf(), listOf())

    fun toEntity(): AllRestaurantsResponse = AllRestaurantsResponse(
        cafes.map { it.toEntity() }, recommendedCafes, favoriteCafes
    )

    companion object {
        fun fromEntity(entity: AllRestaurantsResponse): AllRestaurantsResponseDataEntity =
            with(entity) {
                return AllRestaurantsResponseDataEntity(
                    cafes.map { RestaurantShortDataEntity.fromEntity(it) },
                    recommendedCafes,
                    favoriteCafes
                )
            }
    }
}