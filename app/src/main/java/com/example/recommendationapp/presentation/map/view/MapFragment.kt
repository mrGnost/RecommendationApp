package com.example.recommendationapp.presentation.map.view

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.example.recommendationapp.App
import com.example.recommendationapp.R
import com.example.recommendationapp.databinding.FragmentMapBinding
import com.example.recommendationapp.domain.interactor.DatabaseInteractor
import com.example.recommendationapp.domain.interactor.FilterInteractor
import com.example.recommendationapp.domain.interactor.LocalInteractor
import com.example.recommendationapp.domain.interactor.RecommendationInteractor
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.Filter
import com.example.recommendationapp.domain.model.RestaurantShort
import com.example.recommendationapp.presentation.common.RestaurantPlacemarkClusterView
import com.example.recommendationapp.presentation.common.RestaurantPlacemarkShortView
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModel
import com.example.recommendationapp.presentation.map.viewmodel.MapViewModelFactory
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.presentation.search.view.SearchActivity
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import com.example.recommendationapp.utils.Common
import com.example.recommendationapp.utils.callback.EmptyClickListener
import com.example.recommendationapp.utils.scheduler.SchedulerProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.pow


class MapFragment : Fragment(), CameraListener, MapObjectTapListener, ClusterListener, LocationListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var clustersCollection: ClusterizedPlacemarkCollection
    private lateinit var bottomSheet: MapBottomSheet
    private lateinit var locationManager: LocationManager
    private lateinit var filters: List<Filter>

    private var filtersCount = 0
    private var favouriteIds = listOf<Int>()
    private var recommendedIds: List<Int>? = null
    private var filteredIds: List<Int>? = null
    private var accountEmail = ""

    private var mapPosition: Point = Point(55.75, 37.62)
    private var userPosition: Point = Point(55.75, 37.62)
    private var mapZoom = 20f
    private var visiblePlaces: List<RestaurantShort> = listOf()
    private var expandedId: Int? = null

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

    private val clickListener = object : EmptyClickListener {
        override fun onClick() {
            viewModel.getRecommendedFilter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComp().inject(this)
        bottomSheet = MapBottomSheet.newInstance(clickListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setupBottomSheetCall()
        setupSearchActivityCall()
        setupMapZoom()
        setupLocationUpdates()

        parentFragmentManager.setFragmentResultListener("location", this) { _, bundle ->
            mapPosition = Point(bundle.getDouble("latitude"), bundle.getDouble("longitude"))
            moveMap()
        }

        binding.recommendationsBtn.setOnClickListener {
            binding.recommendationsBtn.isChecked = !binding.recommendationsBtn.isChecked
            viewModel.setRecommendedFilter(binding.recommendationsBtn.isChecked)
            searchRestaurants()
        }

        binding.currentPosFab.setOnClickListener {
            mapPosition = userPosition
            mapZoom = 20f
            moveMap()
        }

        binding.mapview.map.addCameraListener(this)
        mapObjects = binding.mapview.map.mapObjects
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationUpdates() {
        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            TimeUnit.MINUTES.toMillis(5), 100F, this)
    }

    private fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap? {
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

    private fun observeLiveData() {
        viewModel.getRestaurantsLiveData().observe(viewLifecycleOwner, this::drawRestaurants)
        viewModel.getErrorLiveData().observe(viewLifecycleOwner, this::showError)
        viewModel.getFiltersCountLiveData().observe(viewLifecycleOwner, this::displayFiltersCount)
        viewModel.getRecommendedFilterLiveData().observe(viewLifecycleOwner, this::setRecommendedFilter)
        viewModel.getFilteredLiveData().observe(viewLifecycleOwner, this::setFiltered)
        viewModel.getFiltersLiveData().observe(viewLifecycleOwner, this::setFilters)
        viewModel.getFavouriteIdsLiveData().observe(viewLifecycleOwner, this::setFavourite)
        viewModel.getAccountLiveData().observe(viewLifecycleOwner, this::setAccount)
        viewModel.getRecommendedIdsLiveData().observe(viewLifecycleOwner, this::setRecommended)
    }

    private fun setFavourite(list: List<Int>) {
        favouriteIds = list
        viewModel.getAccount()
    }

    private fun setAccount(account: Account) {
        if (account.email == "") {
            if (favouriteIds.isEmpty())
                setRecommended(listOf())
            else
                viewModel.getRecommendedIds(favouriteIds)
        } else {
            viewModel.getRecommendedIds(1)
        }
    }

    private fun setRecommended(list: List<Int>) {
        recommendedIds = list
        binding.recommendationsBtn.setCount(list.size)
        viewModel.putRecommendedToDb(list)
    }

    private fun setFiltered(restaurantIds: List<Int>) {
        filteredIds = restaurantIds
        searchRestaurants()
    }

    private fun drawRestaurants(restaurants: List<RestaurantShort>) {
        Log.d("FINAL", restaurants.map { it.name }.toString())
        visiblePlaces = restaurants
        mapObjects.clear()
        clustersCollection = mapObjects.addClusterizedPlacemarkCollection(this)
        for (place in restaurants) {
            if (place.id == expandedId)
                drawShortInfo(place)
            else
                drawName(place)
        }
        clustersCollection.clusterPlacemarks(60.0, 15)
    }

    private fun drawName(place: RestaurantShort) {
        val location = Point(place.location.latitude, place.location.longitude)
        val obj = clustersCollection.addPlacemark(location)
        val view = RestaurantPlacemarkShortView(requireContext())
        view.setText(place.name)
        val viewProvider = ViewProvider(view)
        obj.setView(viewProvider)
        obj.addTapListener(this@MapFragment)
    }

    private fun drawShortInfo(place: RestaurantShort) {
        binding.restaurantInfoBubble.restaurantName.text = place.name
        binding.restaurantInfoBubble.restaurantAddress.text = place.address
        binding.restaurantInfoBubble.restaurantTags.text = place.categories
        binding.restaurantInfoBubble.restaurantImage.load(Common.getPlaceImageAddress(place.photo)) {
            crossfade(true)
            error(R.drawable.image_broken_24)
            fallback(R.drawable.image_broken_24)
            placeholder(R.drawable.image_placeholder_24)
            transformations(CircleCropTransformation())
            scale(Scale.FIT)
            listener(
                onSuccess = { request, metadata ->
                    val image = createBitmapFromView(binding.restaurantInfoBubble.root, 0, 0)
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

    private fun showError(throwable: Throwable) {
        Log.d(SplashActivity.TAG, "showError() called with: throwable = $throwable")
        Snackbar.make(binding.root, throwable.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
    }

    private fun displayFiltersCount(count: Int) {
        binding.filtersBtn.setCount(count)
        filtersCount = count
    }

    private fun setRecommendedFilter(value: Boolean) {
        binding.recommendationsBtn.isChecked = value
        if (accountEmail == "") {
            viewModel.getFilteredRestaurants(0, filters, false)
        } else {
            viewModel.getFilteredRestaurants(1, filters, value)
        }
    }

    private fun setFilters(filters: List<Filter>) {
        this.filters = filters.filter { it.checked.any { x -> x } }
        Log.d("FILTERS", this.filters.toString())
    }

    private fun searchRestaurants() {
        Log.d("RECOMMENDED", "$recommendedIds")
        Log.d("FILTERED", "$filteredIds")
        if (recommendedIds != null) {
            if (accountEmail != "" && !filteredIds.isNullOrEmpty()) {
                viewModel.getRestaurantsInArea(
                    if (binding.recommendationsBtn.isChecked) filteredIds!! else listOf(),
                    binding.mapview.map.visibleRegion
                )
            } else if (filteredIds == null) {
                viewModel.getRestaurantsInArea(
                    if (binding.recommendationsBtn.isChecked) recommendedIds!! else listOf(),
                    binding.mapview.map.visibleRegion
                )
            } else {
                viewModel.getRestaurantsInArea(
                    if (binding.recommendationsBtn.isChecked)
                        recommendedIds!!.intersect(filteredIds!!.toSet()).toList()
                    else
                        listOf(),
                    binding.mapview.map.visibleRegion
                )
            }
        }
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
        searchRestaurants()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            Log.d("NEW_POSITION", "${cameraPosition.target.latitude}; " +
                    "${cameraPosition.target.longitude}")
            expandedId = null
            mapPosition = binding.mapview.map.cameraPosition.target
            mapZoom = binding.mapview.map.cameraPosition.zoom
            searchRestaurants()
        }
    }

    private fun findClosestPlace(point: Point): RestaurantShort {
        var distance = 10000.0
        var finalPlace: RestaurantShort? = null
        for (place in visiblePlaces) {
            val dist = (point.latitude - place.location.latitude).pow(2) * 3 +
                    (point.longitude - place.location.longitude).pow(2)
            if (dist < distance) {
                distance = dist
                finalPlace = place
            }
        }
        return finalPlace!!
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        if (mapObject is PlacemarkMapObject) {
            val place = findClosestPlace(point)
            if (place.id == expandedId) {
                activity?.startActivity(
                    Intent(activity, RestaurantActivity::class.java)
                        .putExtra("restaurant_id", place.id)
                        .putExtra("restaurant_name", place.name)
                )
                return true
            }
            expandedId = place.id
            drawRestaurants(visiblePlaces)
            return true
        }
        expandedId = null
        drawRestaurants(visiblePlaces)
        return true
    }

    override fun onClusterAdded(cluster: Cluster) {
        val view = RestaurantPlacemarkClusterView(requireContext())
        view.setText(cluster.size.toString())
        cluster.appearance.setView(ViewProvider(view))
        cluster.addClusterTapListener {
            true
        }
    }

    override fun onLocationChanged(location: android.location.Location) {
        userPosition = Point(location.latitude, location.longitude)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
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