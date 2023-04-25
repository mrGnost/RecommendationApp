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

    var restaurantHolderHeight = 0

    fun getPlaceImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/cafes/$id.jpg"

    fun getDishImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/dishes/$id.jpg"
}