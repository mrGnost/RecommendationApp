package com.example.recommendationapp.presentation.restaurant.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var isMarked = false
    private var isFavourite = false
    private var isRecommended = false
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

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
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

        isMarked = intent.getBooleanExtra("is_marked", false)
        isFavourite = intent.getBooleanExtra("is_favourite", false)
        isRecommended = intent.getBooleanExtra("is_recommended", false)

        changeMarkDisplay()
        changeLikeDisplay()

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.mark -> {
                    changeMark(restaurant)
                    true
                }
                R.id.like -> {
                    changeLike(restaurant)
                    true
                }
                else -> false
            }
        }

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
        binding.contentRestaurant.recommendBtn.setOnClickListener {
            changeMark(restaurant)
        }
        binding.contentRestaurant.favouriteBtn.setOnClickListener {
            changeLike(restaurant)
        }
        binding.contentRestaurant.tags.text = restaurant.tags
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun changeMarkDisplay() {
        binding.topAppBar.menu.getItem(0).icon = ContextCompat.getDrawable(
            this,
            if (isMarked) R.drawable.ic_bookmark_circle_24
            else R.drawable.ic_bookmark_border_circle_24
        )
        binding.contentRestaurant.recommendHolder.visibility =
            if (!isMarked && isRecommended) View.VISIBLE else View.GONE
    }

    private fun changeLikeDisplay() {
        binding.topAppBar.menu.getItem(1).icon = ContextCompat.getDrawable(
            this,
            if (isFavourite) R.drawable.ic_favorite_circle_24
            else R.drawable.ic_favorite_border_circle_24
        )
        binding.contentRestaurant.favouriteHolder.visibility =
            if (!isFavourite) View.VISIBLE else View.GONE
    }

    private fun changeMark(restaurant: Restaurant) {
        isMarked = !isMarked
        viewModel.setMark(restaurant.id, isMarked)
        changeMarkDisplay()
    }

    private fun changeLike(restaurant: Restaurant) {
        isFavourite = !isFavourite
        viewModel.setFavourite(restaurant.id, isMarked)
        changeLikeDisplay()
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