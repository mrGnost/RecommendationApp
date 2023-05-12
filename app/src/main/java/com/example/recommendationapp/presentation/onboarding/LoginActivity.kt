package com.example.recommendationapp.presentation.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.ActivityOnboardingLoginBinding
import com.example.recommendationapp.presentation.auth.view.LoginFragment

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.login_fragment_container, LoginFragment.newInstance(), LoginFragment.TAG)
            .commit()
        supportFragmentManager
            .setFragmentResultListener("loginSuccess", this) { _, _ ->
            finish()
        }
        binding.button.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val TAG = "LoginActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): LoginActivity {
            return LoginActivity()
        }
    }
}