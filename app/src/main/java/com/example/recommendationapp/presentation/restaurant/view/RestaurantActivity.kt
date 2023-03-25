package com.example.recommendationapp.presentation.restaurant.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recommendationapp.databinding.FragmentRestaurantBinding

class RestaurantActivity : AppCompatActivity() {
    private lateinit var binding: FragmentRestaurantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}