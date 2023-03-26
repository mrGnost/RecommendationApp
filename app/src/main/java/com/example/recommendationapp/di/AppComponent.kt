package com.example.recommendationapp.di

import android.content.Context
import com.example.recommendationapp.presentation.LauncherActivity
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.map.view.MapBottomSheet
import com.example.recommendationapp.presentation.map.view.MapFragment
import com.example.recommendationapp.presentation.restaurant.view.RestaurantActivity
import com.example.recommendationapp.presentation.search.view.SearchActivity
import com.example.recommendationapp.presentation.splash.view.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RetrofitModule::class,
        RoomModule::class,
        BindModule::class
    ]
)
interface AppComponent {
    fun inject(activity: SplashActivity)
    fun inject(activity: LauncherActivity)
    fun inject(activity: SearchActivity)
    fun inject(activity: RestaurantActivity)
    fun inject(fragment: MapFragment)
    fun inject(fragment: MapBottomSheet)
    fun inject(fragment: FavouriteFragment)


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}