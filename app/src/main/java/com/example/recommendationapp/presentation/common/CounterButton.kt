package com.example.recommendationapp.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.CounterButtonBinding

class CounterButton
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    private val binding: CounterButtonBinding =
        CounterButtonBinding.inflate(LayoutInflater.from(context), this, false)
    private var checkedVal = false

    var isChecked: Boolean
        get() = checkedVal
        set(value) {
            checkedVal = value
            if (checkedVal) {
                binding.buttonLayout.setBackgroundResource(R.drawable.rounded_checked_shape)
                binding.buttonText.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.numberHolder.setBackgroundResource(R.drawable.rounded_active_number_shape)
                binding.number.setTextColor(ContextCompat.getColor(context, R.color.orange_500))
            } else {
                binding.buttonLayout.setBackgroundResource(R.drawable.rounded_unchecked_shape)
                binding.buttonText.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.numberHolder.setBackgroundResource(R.drawable.rounded_inactive_number_shape)
                binding.number.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

    init {
        addView(binding.root)
        val styles = context.obtainStyledAttributes(attributeSet, R.styleable.CounterButton)
        val checked = styles.getBoolean(R.styleable.CounterButton_android_checked, false)
        val text = styles.getText(R.styleable.CounterButton_android_text)
        val number = styles.getInt(R.styleable.CounterButton_number, 0)
        val hasIcon = styles.getBoolean(R.styleable.CounterButton_hasIcon, false)

        isChecked = checked
        binding.buttonText.text = text
        setCount(number)
        binding.buttonIcon.visibility = if (hasIcon) View.VISIBLE else View.GONE

        styles.recycle()
    }

    fun setCount(count: Int) {
        if (count > 0) {
            binding.numberHolder.visibility = View.VISIBLE
            binding.number.text = "$count"
        } else
            binding.numberHolder.visibility = View.GONE
    }
}