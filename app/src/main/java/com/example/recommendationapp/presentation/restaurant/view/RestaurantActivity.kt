package com.example.recommendationapp.presentation.restaurant.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Scale
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentRestaurantBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.restaurant.adapter.ChainAdapter
import com.example.recommendationapp.presentation.restaurant.adapter.PhotoAdapter
import com.example.recommendationapp.presentation.restaurant.adapter.SimilarAdapter
import com.example.recommendationapp.presentation.restaurant.viewmodel.RestaurantViewModel
import com.example.recommendationapp.presentation.restaurant.viewmodel.RestaurantViewModelFactory
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.callback.RestaurantClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RestaurantActivity : AppCompatActivity() {
    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var menu: Menu
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var chainAdapter: ChainAdapter
    private lateinit var similarAdapter: SimilarAdapter
    private lateinit var restaurant: Restaurant
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

    private val clickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort) {
            startActivity(
                Intent(this@RestaurantActivity, RestaurantActivity::class.java)
                    .putExtra("restaurant_id", restaurantShort.id)
                    .putExtra("restaurant_name", restaurantShort.name)
                    .putExtra("is_favourite", restaurantShort.favourite)
                    .putExtra("is_marked", restaurantShort.marked)
                    .putExtra("is_recommended", restaurantShort.recommended)
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (applicationContext as App).appComp().inject(this)
        createViewModel()
        observeLiveData()
        setSupportActionBar(binding.topAppBar)

        viewModel.getRestaurantInfo(intent.getIntExtra("restaurant_id", 1))
        viewModel.getSimilar(intent.getIntExtra("restaurant_id", 1), 10)

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        supportActionBar?.title = intent.getStringExtra("restaurant_name")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.restaurant_top_bar_menu, menu)
        this.menu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.like -> {
                changeLike(restaurant)
                true
            }
            R.id.mark -> {
                changeMark(restaurant)
                true
            }
            else -> false
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
        viewModel.getSimilarLiveData().observe(this, this::showSimilar)
    }

    private fun showProgress(isVisible: Boolean) {
        Log.i(TAG, "showProgress called with param = $isVisible")
        // binding.progressbar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showResults(restaurant: Restaurant) {
        Log.d(TAG_ADD, "showResults() called with: restaurant = ${restaurant.name}")
        this.restaurant = restaurant

        isMarked = intent.getBooleanExtra("is_marked", false)
        isFavourite = intent.getBooleanExtra("is_favourite", false)
        isRecommended = intent.getBooleanExtra("is_recommended", false)

        changeMarkDisplay()
        changeLikeDisplay()

        binding.topImage.load(Common.getPlaceImageAddress(restaurant.photo)) {
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
        setupPhotoRecycler(restaurant.dishPhotos)
        setupChainRecycler(restaurant.chainCafes)
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun showSimilar(places: List<RestaurantShort>) {
        setupSimilarRecycler(places)
    }

    private fun changeMarkDisplay() {
        menu.getItem(0).icon = ContextCompat.getDrawable(
            this,
            if (isMarked) R.drawable.ic_bookmark_circle_24
            else R.drawable.ic_bookmark_border_circle_24
        )
        binding.contentRestaurant.recommendHolder.visibility =
            if (!isMarked && isRecommended) View.VISIBLE else View.GONE
    }

    private fun changeLikeDisplay() {
        menu.getItem(1).icon = ContextCompat.getDrawable(
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

    private fun setupPhotoRecycler(dishes: List<Int>) {
        photoAdapter = PhotoAdapter(dishes)
        binding.contentRestaurant.dishRecycler.adapter = photoAdapter
        binding.contentRestaurant.dishRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupChainRecycler(chain: List<RestaurantShort>) {
        chainAdapter = ChainAdapter(chain, clickListener)
        binding.contentRestaurant.chainRecycler.adapter = chainAdapter
        binding.contentRestaurant.chainRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupSimilarRecycler(places: List<RestaurantShort>) {
        similarAdapter = SimilarAdapter(places, clickListener)
        binding.contentRestaurant.similarRecycler.adapter = similarAdapter
        binding.contentRestaurant.similarRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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