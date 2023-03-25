package com.example.recommendationapp.presentation.search.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.recommendationapp.databinding.FragmentSearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setTransitionName(binding.searchInput, "search_input")
        binding.cancelButton.setOnClickListener {
            supportFinishAfterTransition()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.searchEditText.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        supportFinishAfterTransition()
    }
}