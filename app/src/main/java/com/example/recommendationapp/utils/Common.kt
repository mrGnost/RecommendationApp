package com.example.recommendationapp.utils

import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd

object Common {
    const val LOCATION_PERMISSION_REQUEST_CODE = 612
    const val LOCATION_PERMISSION_RATIONALE_CODE = 413

    const val PREFERENCE_NAME = "RECOMMENDATION_PREF"

    const val PREF_ACCOUNT_NAME = "PREF_ACCOUNT_NAME"
    const val PREF_ACCOUNT_TOKEN = "PREF_ACCOUNT_TOKEN"
    const val PREF_ONBOARDING_VIEWED = "PREF_ONBOARDING_VIEWED"

    var restaurantHolderHeight = 0

    fun getPlaceImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/cafes/$id.jpg"

    fun getDishImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/dishes/$id.jpg"

    fun getTokenHeader(token: String) = "Bearer $token"
}