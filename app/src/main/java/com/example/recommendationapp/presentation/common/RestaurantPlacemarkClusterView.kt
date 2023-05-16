package com.example.recommendationapp.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.recommendationapp.databinding.MapClusterRestaurantBinding

class RestaurantPlacemarkClusterView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private val binding: MapClusterRestaurantBinding =
        MapClusterRestaurantBinding.inflate(LayoutInflater.from(context), this, false)

    init {
        addView(binding.root)
    }

    fun setText(text: String) {
        binding.number.text = text
    }
}