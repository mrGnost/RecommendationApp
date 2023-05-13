package com.example.recommendationapp.presentation.map.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recommendationapp.App
import com.example.recommendationapp.databinding.BottomSheetMapBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.FilterInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.presentation.map.adapter.FiltersAdapter
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModel
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModelFactory
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.utils.callback.EmptyClickListener
import com.example.recommendationapp.utils.callback.FilterClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MapBottomSheet(private val clickListener: EmptyClickListener) : BottomSheetDialogFragment() {
    private lateinit var adapter: FiltersAdapter
    lateinit var binding: BottomSheetMapBinding
    lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private val disposables = CompositeDisposable()

    private lateinit var filters: List<Filter>

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var filterInteractor: FilterInteractor
    @Inject
    lateinit var localInteractor: LocalInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(
            recommendationInteractor, databaseInteractor, filterInteractor, localInteractor, schedulers)
    }

    private val chipClickListener = object : FilterClickListener {
        override fun onClick(filter: Filter, pos: Int, value: Boolean) {
            viewModel.setFilterChecked(filter, value, pos)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComp().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        createAdapter()
        viewModel.getRecommendedFilter()
        binding.closeBtn.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.cancelBtn.setOnClickListener {
            viewModel.clearAllFilters(filters)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.showBtn.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.recommendationsChip.setOnClickListener {
            viewModel.setRecommendedFilter(binding.recommendationsChip.isChecked)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    private fun observeLiveData() {
        viewModel.getFiltersLiveData().observe(viewLifecycleOwner, this::setFilters)
        viewModel.getFiltersCountLiveData().observe(viewLifecycleOwner, this::setFiltersCount)
        viewModel.getRecommendedFilterLiveData().observe(viewLifecycleOwner, this::setRecommendedFilter)
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
    }

    private fun setFilters(filters: List<Filter>) {
        this.filters = filters
        adapter.setData(filters)
    }

    private fun setFiltersCount(count: Int) {
        binding.showBtn.setCount(count)
        if (count == 0) {
            binding.cancelBtn.visibility = View.GONE
            binding.showBtn.text = "Показать места"
        } else {
            binding.cancelBtn.visibility = View.VISIBLE
            binding.showBtn.text = "Показать"
        }
    }

    private fun setRecommendedFilter(value: Boolean) {
        binding.recommendationsChip.isChecked = value
    }

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun createAdapter() {
        adapter = FiltersAdapter(emptyList(), chipClickListener)
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        clickListener.onClick()
        disposables.dispose()
        disposables.clear()
        Log.d("BOTTOM_SHEET", "destroyed")
    }

    companion object {
        const val TAG = "MapBottomSheet"
        private const val TAG_ADD = "${MapFragment.TAG} ADD"
        private const val TAG_ERROR = "${MapFragment.TAG} ERROR"
        private const val TAG_PROGRESS = "${MapFragment.TAG} PROGRESS"

        fun newInstance(clickListener: EmptyClickListener): MapBottomSheet {
            return MapBottomSheet(clickListener)
        }
    }
}