package com.example.recommendationapp.presentation.favourite.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationapp.App
import com.example.recommendationapp.databinding.FragmentFavouriteBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.favourite.adapter.FavouriteAdapter
import com.example.recommendationapp.presentation.favourite.viewmodel.FavouriteViewModel
import com.example.recommendationapp.presentation.favourite.viewmodel.FavouriteViewModelFactory
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.utils.callback.RestaurantClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var adapter: FavouriteAdapter
    private var favData = listOf<RestaurantShort>()
    private var markData = listOf<RestaurantShort>()
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    private var holderClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort) {
            startActivity(
                Intent(activity, RestaurantActivity::class.java)
                    .putExtra("restaurant_id", restaurantShort.id)
                    .putExtra("is_favourite", restaurantShort.favourite)
                    .putExtra("is_marked", restaurantShort.marked)
                    .putExtra("is_recommended", restaurantShort.recommended)
            )
        }
    }

    private var markClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort) {
            if (binding.favSwitch.isFavourite) {
                viewModel.clearLike(restaurantShort)
            } else {
                viewModel.clearMark(restaurantShort)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComp().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createAdapter()
        createViewModel()
        observeLiveData()

        binding.favSwitch.setOnSwitchChangeListener {
            val favourite = binding.favSwitch.isFavourite
            adapter.setData(if (favourite) favData else markData, favourite)
        }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this, FavouriteViewModelFactory(recommendationInteractor, databaseInteractor, schedulers)
        )[FavouriteViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getRestaurantsLiveData(true).observe(viewLifecycleOwner, this::updateLikes)
        viewModel.getRestaurantsLiveData(false).observe(viewLifecycleOwner, this::updateMarks)
    }

    private fun updateLikes(restaurants: List<RestaurantShort>) {
        if (binding.favSwitch.isFavourite) {
            adapter.setData(restaurants, true)
        }
        favData = restaurants
        Log.d("UPDATED_LIKES", "$favData")
    }

    private fun updateMarks(restaurants: List<RestaurantShort>) {
        if (!binding.favSwitch.isFavourite) {
            adapter.setData(restaurants, false)
        }
        markData = restaurants
        Log.d("UPDATED_MARKS", "$markData")
    }

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun createAdapter() {
        adapter = FavouriteAdapter(
            listOf(),
            holderClickListener,
            markClickListener
        )
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        disposables.clear()
    }

    companion object {
        const val TAG = "FavouriteFragment"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): FavouriteFragment {
            return FavouriteFragment()
        }
    }
}