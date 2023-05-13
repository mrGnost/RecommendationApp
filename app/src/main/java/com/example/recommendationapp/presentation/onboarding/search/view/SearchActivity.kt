package com.example.recommendationapp.presentation.onboarding.search.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.ActivityOnboardingSearchBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.onboarding.finish.view.FinishActivity
import com.example.recommendationapp.presentation.onboarding.LoginActivity
import com.example.recommendationapp.presentation.onboarding.WelcomeActivity
import com.example.recommendationapp.presentation.onboarding.search.adapter.SearchAdapter
import com.example.recommendationapp.presentation.onboarding.search.viewmodel.SearchViewModel
import com.example.recommendationapp.presentation.onboarding.search.viewmodel.SearchViewModelFactory
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.utils.callback.RestaurantClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter

    private var removedPosition = -1
    private var likedIds = listOf<Int>()

    private var holderClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort, position: Int) {
            startActivity(
                Intent(this@SearchActivity, RestaurantActivity::class.java)
                    .putExtra("restaurant_id", restaurantShort.id)
                    .putExtra("restaurant_name", restaurantShort.name)
            )
        }
    }

    private var markClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort, position: Int) {
            viewModel.clearLike(restaurantShort)
            removedPosition = position
        }
    }

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
        binding = ActivityOnboardingSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createAdapter()
        createViewModel()
        observeLiveData()
        setupSearchActivityCall()

        binding.nextButton.setOnClickListener {
            viewModel.getAccount()
        }
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun createAdapter() {
        adapter = SearchAdapter(
            listOf(),
            holderClickListener,
            markClickListener
        )
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(recommendationInteractor, databaseInteractor, localInteractor, schedulers)
        )[SearchViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(this, this::showError)
        viewModel.getRestaurantIdsLiveData().observe(this, this::updateLikes)
        viewModel.getRestaurantsLiveData().observe(this, this::updateAdapter)
        viewModel.getAccountLiveData().observe(this, this::cacheRecommendations)
        viewModel.getRecommendationSavedLiveData().observe(this, this::goNext)
    }

    private fun updateLikes(ids: List<Int>) {
        likedIds = ids
        viewModel.getRestaurantsByIds(ids)
    }

    private fun updateAdapter(restaurants: List<RestaurantShort>) {
        binding.searchHeader.visibility = if (restaurants.isEmpty()) View.VISIBLE else View.GONE
        binding.loginSection.visibility = if (restaurants.isEmpty()) View.VISIBLE else View.GONE
        binding.restaurantListHolder.visibility = if (restaurants.isEmpty()) View.GONE else View.VISIBLE
        customizeButton(restaurants.isNotEmpty())
        if (removedPosition == -1)
            adapter.setData(restaurants)
        else
            adapter.removeItem(restaurants, removedPosition)
        Log.d("UPDATED_LIKES", "$restaurants")
    }

    private fun cacheRecommendations(account: Account) {
        if (account.email == "") {
            viewModel.cacheRecommendedIds(likedIds)
        } else {
            viewModel.cacheRecommendedIds(1)
        }
    }

    private fun goNext(finished: Boolean) {
        if (finished) {
            startActivity(Intent(this, FinishActivity::class.java))
            finish()
        }
    }

    private fun customizeButton(isNext: Boolean) {
        if (isNext) {
            binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange_500))
            binding.nextButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.nextButton.text = "Подобрать рекомендации"
        } else {
            binding.nextButton.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_200))
            binding.nextButton.setTextColor(ContextCompat.getColor(this, R.color.gray_700))
            binding.nextButton.text = "Пропустить"
        }
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun setupSearchActivityCall() {
        ViewCompat.setTransitionName(binding.searchInput, "map_search_input")
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                callSearchActivity()
            }
        }
        binding.searchEditText.setOnClickListener {
            callSearchActivity()
        }
    }

    private fun callSearchActivity() {
        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, binding.searchInput, "search_input")
        startActivity(Intent(
            this,
            com.example.recommendationapp.presentation.search.view.SearchActivity::class.java
        ), options.toBundle())
    }

    override fun onBackPressed() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    companion object {
        const val TAG = "OnboardingSearchActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): SearchActivity {
            return SearchActivity()
        }
    }
}