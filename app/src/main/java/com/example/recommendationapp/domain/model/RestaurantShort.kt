package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RestaurantShort(
    val id: Int,
    val name: String,
    val photo: Int,
    val address: String,
    val location: Location,
    val categories: String
) : Parcelable
