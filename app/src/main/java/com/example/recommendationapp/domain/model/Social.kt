package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Social(
    val type: String,
    val link: String
) : Parcelable
