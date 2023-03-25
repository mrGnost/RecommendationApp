package com.example.recommendationapp.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.SwitchFavouriteBinding

class FavouriteSwitch
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {
    private val binding: SwitchFavouriteBinding =
        SwitchFavouriteBinding.inflate(LayoutInflater.from(context), this, false)
    private var favourite = true

    var isFavourite: Boolean
        get() = favourite
        set(value) {
            favourite = value
            if (value) {
                binding.favChip.setChipBackgroundColorResource(R.color.white)
                binding.markChip.setChipBackgroundColorResource(R.color.orange_200)
            } else {
                binding.favChip.setChipBackgroundColorResource(R.color.orange_200)
                binding.markChip.setChipBackgroundColorResource(R.color.white)
            }
        }

    init {
        addView(binding.root)
        isFavourite = true
        binding.favChip.setOnClickListener {
            isFavourite = true
        }
        binding.markChip.setOnClickListener {
            isFavourite = false
        }
    }
}