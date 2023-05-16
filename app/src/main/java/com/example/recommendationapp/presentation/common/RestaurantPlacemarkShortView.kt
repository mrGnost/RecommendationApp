package com.example.recommendationapp.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.recommendationapp.databinding.MapNameRestaurantBinding

class RestaurantPlacemarkShortView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private val binding: MapNameRestaurantBinding =
        MapNameRestaurantBinding.inflate(LayoutInflater.from(context), this, false)

    init {
        addView(binding.root)
    }

    fun setText(text: String) {
        binding.restaurantText.text = text
    }
}