package com.example.recommendationapp.presentation.onboarding.finish.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.ActivityOnboardingFinishBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.FilterInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.onboarding.finish.viewmodel.FinishViewModel
import com.example.recommendationapp.presentation.onboarding.finish.viewmodel.FinishViewModelFactory
import com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModel
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModelFactory
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import javax.inject.Inject

class FinishActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingFinishBinding
    private lateinit var viewModel: FinishViewModel

    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createViewModel()
        observeLiveData()
        viewModel.getRecommendedCount()

        binding.finishButton.setOnClickListener {
            viewModel.setOnboardingFinished()
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this, FinishViewModelFactory(databaseInteractor, localInteractor, schedulers)
        )[FinishViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getRecommendedCountLiveData().observe(this, this::displayCount)
    }

    private fun displayCount(count: Int) {
        binding.finishDescription.text = getString(R.string.recommended_number_found, count)
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