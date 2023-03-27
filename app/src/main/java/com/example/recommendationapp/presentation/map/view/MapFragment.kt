package com.example.recommendationapp.presentation.map.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentMapBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.LocationInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Location
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModel
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModelFactory
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.presentation.search.view.SearchActivity
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.runtime.image.ImageProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MapFragment : Fragment(), CameraListener, MapObjectTapListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: MapViewModel
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var bottomSheet: MapBottomSheet
    private val disposables = CompositeDisposable()

    private var mapPosition: Point = Point(55.87, 37.7)
    private var mapZoom = 16f

    @Inject
    lateinit var recommendationInteractor: RecommendationInteractor
    @Inject
    lateinit var databaseInteractor: DatabaseInteractor
    @Inject
    lateinit var locationInteractor: LocationInteractor
    @Inject
    lateinit var schedulers: SchedulerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComp().inject(this)
        bottomSheet = MapBottomSheet.newInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewModel()
        observeLiveData()
        setupBottomSheetCall()
        setupSearchActivityCall()
        setupMapZoom()

        binding.recommendationsBtn.setOnClickListener {
            binding.recommendationsBtn.isChecked = !binding.recommendationsBtn.isChecked
            viewModel.getRestaurantsInArea(
                binding.recommendationsBtn.isChecked,
                binding.mapview.map.visibleRegion
            )
        }

        binding.currentPosFab.setOnClickListener {
            viewModel.getCurrentLocation()
        }

        binding.mapview.map.addCameraListener(this)
        mapObjects = binding.mapview.map.mapObjects
        moveMap()
    }

    fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap? {
        if (width > 0 && height > 0) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    width, View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(
                    height, View.MeasureSpec.EXACTLY
                )
            )
        }
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val background = view.background
        background?.draw(canvas)
        view.draw(canvas)
        return bitmap
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(
            this, MapViewModelFactory(
                recommendationInteractor, databaseInteractor, locationInteractor, schedulers)
        )[MapViewModel::class.java]
    }

    private fun observeLiveData() {
        viewModel.getRestaurantsLiveData().observe(viewLifecycleOwner, this::drawRestaurants)
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getLocationLiveData().observe(viewLifecycleOwner, this::moveToLocation)
    }

    private fun drawRestaurants(restaurants: List<RestaurantShort>) {
        mapObjects.clear()
        for (place in restaurants) {
            binding.restaurantBubble.restaurantName.text = place.name
            binding.restaurantBubble.restaurantAddress.text = place.address
            binding.restaurantBubble.restaurantTags.text = place.categories
            binding.restaurantBubble.restaurantImage.load(Common.getImageAddress(place.photo)) {
                crossfade(true)
                error(R.drawable.image_broken_24)
                fallback(R.drawable.image_broken_24)
                placeholder(R.drawable.image_placeholder_24)
                transformations(CircleCropTransformation())
                scale(Scale.FIT)
                listener(
                    onSuccess = { request, metadata ->
                        val image = createBitmapFromView(binding.restaurantBubble.root, 0, 0)
                        mapObjects.addPlacemark(
                            Point(place.location.latitude, place.location.longitude),
                            ImageProvider.fromBitmap(image)
                        ).apply {
                            addTapListener(this@MapFragment)
                        }
                    }
                )
            }

        }
    }

    private fun moveToLocation(location: Location) {
        Log.d("LOCATION_ACQUIRED", "${location.latitude}, ${location.longitude}")
        mapPosition = Point(location.latitude, location.longitude)
        moveMap()
    }

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun setupBottomSheetCall() {
        binding.filtersBtn.setOnClickListener {
            bottomSheet.show(parentFragmentManager, MapBottomSheet.TAG)
        }
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

    private fun setupMapZoom() {
        binding.zoomInFab.setOnClickListener {
            ++mapZoom
            moveMap()
        }
        binding.zoomOutFab.setOnClickListener {
            if (mapZoom > 0) {
                --mapZoom
                moveMap()
            }
        }
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    private fun callSearchActivity() {
        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(activity as LauncherActivity, binding.searchInput, "search_input")
        activity?.startActivity(Intent(activity, SearchActivity::class.java), options.toBundle())
    }

    private fun moveMap() {
        binding.mapview.map.move(
            CameraPosition(mapPosition, mapZoom, 0.0f, 45.0f),
            Animation(Animation.Type.SMOOTH, .2f),
            null
        )
        viewModel.getRestaurantsInArea(
            binding.recommendationsBtn.isChecked,
            binding.mapview.map.visibleRegion
        )
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            viewModel.getRestaurantsInArea(
                binding.recommendationsBtn.isChecked,
                binding.mapview.map.visibleRegion
            )
        }
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        if (mapObject is PlacemarkMapObject)
            activity?.startActivity(Intent(activity, RestaurantActivity::class.java))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        disposables.clear()
    }

    companion object {
        const val TAG = "MapFragment"
        private const val TAG_ADD = "$TAG ADD"
        private const val TAG_ERROR = "$TAG ERROR"
        private const val TAG_PROGRESS = "$TAG PROGRESS"

        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}