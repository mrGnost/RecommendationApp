package com.example.recommendationapp.presentation.restaurant.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.size.Scale
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentRestaurantBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.restaurant.adapter.PhotoAdapter
import com.example.recommendationapp.presentation.restaurant.adapter.SimilarAdapter
import com.example.recommendationapp.presentation.restaurant.viewmodel.RestaurantViewModel
import com.example.recommendationapp.presentation.restaurant.viewmodel.RestaurantViewModelFactory
import com.example.recommendationapp.presentation.search.adapter.SearchAdapter
import com.example.recommendationapp.presentation.search.view.SearchActivity
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModel
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModelFactory
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RestaurantActivity : AppCompatActivity() {
    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var similarAdapter: SimilarAdapter
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (applicationContext as App).appComp().inject(this)
        createViewModel()
        observeLiveData()

        viewModel.getRestaurantInfo(intent.getIntExtra("restaurant_id", 1))
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this, RestaurantViewModelFactory(recommendationInteractor, databaseInteractor, schedulers)
        )[RestaurantViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(this, this::showError)
        viewModel.getProgressLiveData().observe(this, this::showProgress)
        viewModel.getRestaurantLiveData().observe(this, this::showResults)
    }

    private fun showProgress(isVisible: Boolean) {
        Log.i(TAG, "showProgress called with param = $isVisible")
        // binding.progressbar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showResults(restaurant: Restaurant) {
        Log.d(TAG_ADD, "showResults() called with: restaurant = ${restaurant.name}")
        val isMarked = intent.getBooleanExtra("is_marked", false)
        val isFavourite = intent.getBooleanExtra("is_favourite", false)

        supportActionBar?.title = restaurant.name
        binding.topAppBar.title = restaurant.name
        Log.d("SUPPORT_BAR", "${supportActionBar?.title}")
        Log.d("TOP_BAR", "${binding.topAppBar.title}")
        binding.topImage.load(Common.getImageAddress(restaurant.photo)) {
            crossfade(true)
            error(R.drawable.image_broken_24)
            fallback(R.drawable.image_broken_24)
            placeholder(R.drawable.image_placeholder_24)
            scale(Scale.FIT)
        }
        binding.contentRestaurant.address.text = restaurant.address
        binding.contentRestaurant.workHours.text = restaurant.workingHours
        if (isMarked)
            binding.contentRestaurant.recommendHolder.visibility = View.GONE
        else {
            binding.contentRestaurant.recommendBtn.setOnClickListener {
                viewModel.setMark(restaurant.id, true)
                binding.contentRestaurant.recommendHolder.visibility = View.GONE
            }
        }
        if (isFavourite)
            binding.contentRestaurant.favouriteHolder.visibility = View.GONE
        else {
            binding.contentRestaurant.favouriteBtn.setOnClickListener {
                viewModel.setFavourite(restaurant.id, true)
                binding.contentRestaurant.favouriteHolder.visibility = View.GONE
            }
        }
        binding.contentRestaurant.tags.text = restaurant.tags
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "RestaurantActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): RestaurantActivity {
            return RestaurantActivity()
        }
    }
}