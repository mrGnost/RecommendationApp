package com.example.recommendationapp.presentation.search.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recommendationapp.App
import com.example.recommendationapp.databinding.FragmentSearchBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.presentation.search.adapter.SearchAdapter
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModel
import com.example.recommendationapp.presentation.search.viewmodel.SearchViewModelFactory
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.utils.callback.RestaurantClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
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
                Intent(this@SearchActivity, RestaurantActivity::class.java)
                    .putExtra("restaurant_id", restaurantShort.id)
                    .putExtra("restaurant_name", restaurantShort.name)
                    .putExtra("is_favourite", restaurantShort.favourite)
                    .putExtra("is_marked", restaurantShort.marked)
                    .putExtra("is_recommended", restaurantShort.recommended)
            )
        }
    }

    private var markClickListener = object : RestaurantClickListener {
        override fun onClick(restaurantShort: RestaurantShort) {
            viewModel.changeLike(restaurantShort, !restaurantShort.favourite)
            val text = binding.searchEditText.text?.trim()
            if (text != null && text.isNotBlank())
                viewModel.search(text.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (applicationContext as App).appComp().inject(this)
        ViewCompat.setTransitionName(binding.searchInput, "search_input")
        binding.cancelButton.setOnClickListener {
            supportFinishAfterTransition()
        }
        createAdapter()
        createViewModel()
        observeLiveData()

        disposables.add(Observable.create { subscriber ->
            binding.searchEditText.doOnTextChanged { input, _, _, _ ->
                subscriber.onNext(input.toString())
            }
        }
            .map { text -> text.trim() }
            .debounce(250, TimeUnit.MILLISECONDS)
            .filter { text -> text.isNotBlank() }
            .distinctUntilChanged()
            .subscribe { text ->
                viewModel.search(text)
            })
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this, SearchViewModelFactory(recommendationInteractor, databaseInteractor, schedulers)
        )[SearchViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getErrorLiveData().observe(this, this::showError)
        viewModel.getProgressLiveData().observe(this, this::showProgress)
        viewModel.getResultLiveData().observe(this, this::showResults)
    }

    private fun showProgress(isVisible: Boolean) {
        Log.i(TAG, "showProgress called with param = $isVisible")
        // binding.progressbar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showResults(list: List<RestaurantShort>) {
        Log.d(TAG_ADD, "showData() called with: list = $list")
        adapter.setData(list)
    }

    private fun showError(throwable: Throwable) {
        Log.d(TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun createAdapter() {
        adapter = SearchAdapter(emptyList(), holderClickListener, markClickListener)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        binding.searchEditText.requestFocus()
    }

    override fun onStop() {
        super.onStop()
        supportFinishAfterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        disposables.clear()
    }

    companion object {
        const val TAG = "SearchActivity"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): SearchActivity {
            return SearchActivity()
        }
    }
}