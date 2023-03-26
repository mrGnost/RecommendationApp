package com.example.recommendationapp.domain.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Filter(
    val id: Int,
    val name: String,
    val variants: List<String>,
    val checked: List<Boolean>
) : Parcelable