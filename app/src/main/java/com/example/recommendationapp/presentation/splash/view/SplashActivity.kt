package com.example.recommendationapp.presentation.splash.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recommendationapp.App
import com.example.recommendationapp.databinding.ActivitySplashBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AccountLocal
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.onboarding.WelcomeActivity
import com.example.recommendationapp.presentation.splash.viewmodel.SplashViewModel
import com.example.recommendationapp.presentation.splash.viewmodel.SplashViewModelFactory
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel
    private val disposables = CompositeDisposable()

    private lateinit var account: AccountLocal

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComp().inject(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createViewModel()
        observeLiveData()
        viewModel.cacheAllRestaurants()
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            SplashViewModelFactory(
                recommendationInteractor, databaseInteractor, localInteractor, schedulers
            )
        )[SplashViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getProgressLiveData().observe(this, this::showProgress)
        viewModel.getCompleteLiveData().observe(this, this::cacheComplete)
        viewModel.getErrorLiveData().observe(this, this::showError)
        viewModel.getOnboardingFinishedLiveData().observe(this, this::onboardingChecked)
        viewModel.getAccountLiveData().observe(this, this::accountFound)
        viewModel.getRecommendationsCompleteLiveData().observe(this, this::recommendedSet)
        viewModel.getFavouritesCompleteLiveData().observe(this, this::favouriteSet)
        viewModel.getFiltersCompleteLiveData().observe(this, this::filtersSet)
    }

    private fun showProgress(isVisible: Boolean) {
        Log.i(TAG, "showProgress called with param = $isVisible")
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun cacheComplete(isComplete: Boolean) {
        Log.d(TAG_ADD, "cache loaded: $isComplete")
        if (isComplete) {
            viewModel.getFilters()
        }
    }

    private fun onboardingChecked(finished: Boolean) {
        if (finished) {
            viewModel.getAccount()
        } else {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun accountFound(account: AccountLocal) {
        this.account = account
        if (account.email == "") {
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        } else {
            viewModel.getRecommendations(account.token)
        }
    }

    private fun recommendedSet(isComplete: Boolean) {
        Log.d(TAG_ADD, "recommended loaded: $isComplete")
        if (isComplete)
            viewModel.getFavourites(account.token)
    }

    private fun favouriteSet(isComplete: Boolean) {
        Log.d(TAG_ADD, "favourite loaded: $isComplete")
        if (isComplete) {
            startActivity(Intent(this, LauncherActivity::class.java))
            finish()
        }
    }

    private fun filtersSet(isComplete: Boolean) {
        Log.d(TAG_ADD, "filters loaded: $isComplete")
        if (isComplete) {
            viewModel.getOnboardingFinished()
        }
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        disposables.clear()
    }

    companion object {
        const val TAG = "SplashActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): SplashActivity {
            return SplashActivity()
        }
    }
}