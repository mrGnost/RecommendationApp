package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: Int,
    val name: String,
    val photo: Int,
    val dishPhotos: List<Int>,
    val description: String,
    val workingHours: String,
    val address: String,
    val contactPhoneNumbers: String,
    val location: Location,
    val rating: Double,
    val categories: String,
    val tags: String,
    val socials: List<Social>,
    val chainCafes: List<RestaurantShort>
) : Parcelable
