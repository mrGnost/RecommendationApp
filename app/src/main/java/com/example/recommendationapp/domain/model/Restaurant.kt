package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: Int,
    val name: String,
    val photo: Int,
    val description: String,
    val workingHours: String,
    val contactPhoneNumbers: String,
    val location: Location,
    val rating: Double,
    val priceCategory: String,
    val categories: String,
    val socials: List<Social>?
) : Parcelable
