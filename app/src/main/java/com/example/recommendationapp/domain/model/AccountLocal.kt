package com.example.recommendationapp.domain.model

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class AccountLocal(
    val email: String,
    val token: String,
): Parcelable