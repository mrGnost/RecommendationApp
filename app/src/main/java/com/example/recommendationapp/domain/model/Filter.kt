package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(
    val id: Int,
    val name: String,
    val variants: List<String>
) : Parcelable