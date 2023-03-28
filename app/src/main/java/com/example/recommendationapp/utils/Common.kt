package com.example.recommendationapp.utils

object Common {
    const val LOCATION_PERMISSION_REQUEST_CODE = 612
    const val LOCATION_PERMISSION_RATIONALE_CODE = 413

    fun getPlaceImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/cafes/$id.jpg"

    fun getDishImageAddress(id: Int) = "http://elesinsv.fvds.ru:8080/assets/dishes/$id.jpg"
}