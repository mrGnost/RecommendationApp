package com.example.recommendationapp.domain.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Account(
    val email: String,
    val password: String,
): Parcelable
