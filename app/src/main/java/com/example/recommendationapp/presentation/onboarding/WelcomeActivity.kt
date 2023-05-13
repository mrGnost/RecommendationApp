package com.example.recommendationapp.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recommendationapp.App
import com.example.recommendationapp.databinding.ActivityWelcomeBinding
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComp().inject(this)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (applicationContext as App).appComp().inject(this)
        bindNextPage()
    }

    private fun bindNextPage() {
        binding.next.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            finish()
        }
    }

    companion object {
        const val TAG = "WelcomeActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): WelcomeActivity {
            return WelcomeActivity()
        }
    }
}