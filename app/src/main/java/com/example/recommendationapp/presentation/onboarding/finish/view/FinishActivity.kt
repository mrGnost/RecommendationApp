package com.example.recommendationapp.presentation.onboarding.finish.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recommendationapp.databinding.ActivityOnboardingFinishBinding
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity

class FinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finishButton.setOnClickListener {
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    companion object {
        const val TAG = "FinishActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): FinishActivity {
            return FinishActivity()
        }
    }
}