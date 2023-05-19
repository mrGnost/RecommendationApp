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
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentFavouriteBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.AccountLocal
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

    private var removedPosition = -1
    private var account: AccountLocal? = null

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    private var holderClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort, position: Int) {
            startActivity(
                Intent(activity, RestaurantActivity::class.java)
                    .putExtra("restaurant_id", restaurantShort.id)
                    .putExtra("restaurant_name", restaurantShort.name)
            )
        }
    }

    private var markClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort, position: Int) {
            if (binding.favSwitch.isFavourite) {
                viewModel.clearLike(restaurantShort, account)
            } else {
                viewModel.clearMark(restaurantShort, account)
            }
            removedPosition = position
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

        viewModel.getAccount()

        binding.favSwitch.setOnSwitchChangeListener {
            val favourite = binding.favSwitch.isFavourite
            setEmptyMessage(favourite)
            adapter.setData(if (favourite) favData else markData, favourite)
            showEmptyMessage(if (favourite) favData.isEmpty() else markData.isEmpty())
        }
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this,
            FavouriteViewModelFactory(
                recommendationInteractor, databaseInteractor, localInteractor, schedulers
            )
        )[FavouriteViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getAccountLiveData().observe(viewLifecycleOwner, this::setAccount)
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getRestaurantIdsLiveData(true).observe(viewLifecycleOwner, this::putLikeIds)
        viewModel.getRestaurantIdsLiveData(false).observe(viewLifecycleOwner, this::putMarkIds)
        viewModel.getFavouriteLiveData().observe(viewLifecycleOwner, this::updateLikes)
        viewModel.getMarkedLiveData().observe(viewLifecycleOwner, this::updateMarks)
    }

    private fun setAccount(account: AccountLocal) {
        if (account.email != "")
            this.account = account
    }

    private fun putLikeIds(restaurantIds: List<Int>) {
        viewModel.getFavouritesByIds(restaurantIds)
    }

    private fun putMarkIds(restaurantIds: List<Int>) {
        viewModel.getMarkedByIds(restaurantIds)
    }

    private fun updateLikes(restaurants: List<RestaurantShort>) {
        if (binding.favSwitch.isFavourite) {
            if (removedPosition == -1)
                adapter.setData(restaurants, true)
            else
                adapter.removeItem(restaurants, removedPosition)
            showEmptyMessage(restaurants.isEmpty())
        }
        favData = restaurants
        Log.d("UPDATED_LIKES", "$favData")
    }

    private fun updateMarks(restaurants: List<RestaurantShort>) {
        if (!binding.favSwitch.isFavourite) {
            if (removedPosition == -1)
                adapter.setData(restaurants, false)
            else
                adapter.removeItem(restaurants, removedPosition)
            showEmptyMessage(restaurants.isEmpty())
        }
        markData = restaurants
        Log.d("UPDATED_MARKS", "$markData")
    }

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun showEmptyMessage(isEmpty: Boolean) {
        binding.recycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyHolder.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun setEmptyMessage(favourite: Boolean) {
        if (favourite) {
            binding.emptyImage.setImageResource(R.drawable.ic_fav_nav_24)
            binding.emptyMessage.text = getString(R.string.fav_empty_message)
            binding.emptyDescription.text = getString(R.string.fav_empty_comment)
        } else {
            binding.emptyImage.setImageResource(R.drawable.ic_bookmark_dark_24)
            binding.emptyMessage.text = getString(R.string.mark_empty_message)
            binding.emptyDescription.text = getString(R.string.mark_empty_comment)
        }
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