package com.example.recommendationapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token(
    val token: String
) : Parcelable
