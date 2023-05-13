package com.example.recommendationapp.di

import android.content.Context
import com.example.recommendationapp.databinding.ActivityOnboardingFinishBinding
import com.example.recommendationapp.databinding.ActivityOnboardingLoginBinding
import com.example.recommendationapp.databinding.ActivityOnboardingSearchBinding
import com.example.recommendationapp.presentation.auth.view.AuthFragment
import com.example.recommendationapp.presentation.auth.view.LoginFragment
import com.example.recommendationapp.presentation.auth.view.RegisterFragment
import com.example.recommendationapp.presentation.launcher.view.LauncherActivity
import com.example.recommendationapp.presentation.favourite.view.FavouriteFragment
import com.example.recommendationapp.presentation.map.view.MapBottomSheet
import com.example.recommendationapp.presentation.map.view.MapFragment
import com.example.recommendationapp.presentation.onboarding.LoginActivity
import com.example.recommendationapp.presentation.onboarding.WelcomeActivity
import com.example.recommendationapp.presentation.onboarding.finish.view.FinishActivity
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
        FilterModule::class,
        LocalModule::class,
        BindModule::class
    ]
)
interface AppComponent {
    fun inject(activity: SplashActivity)
    fun inject(activity: LauncherActivity)
    fun inject(activity: SearchActivity)
    fun inject(activity: RestaurantActivity)
    fun inject(activity: WelcomeActivity)
    fun inject(activity: com.example.recommendationapp.presentation.onboarding.search.view.SearchActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: FinishActivity)
    fun inject(fragment: MapFragment)
    fun inject(fragment: MapBottomSheet)
    fun inject(fragment: FavouriteFragment)
    fun inject(fragment: AuthFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegisterFragment)


    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}