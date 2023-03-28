package com.example.recommendationapp.utils.callback

import com.example.recommendationapp.domain.model.Filter

interface FilterClickListener {
    fun onClick(filter: Filter, pos: Int, value: Boolean)
}