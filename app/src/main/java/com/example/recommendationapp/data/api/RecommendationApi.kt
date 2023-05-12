package com.example.recommendationapp.data.api

import com.example.recommendationapp.data.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RecommendationApi {
    @GET("cafes/{cafe_id}")
    fun getRestaurantInfo(@Path("cafe_id") id: Int): Single<RestaurantDataEntity>

    @GET("cafes")
    fun getAllRestaurants(): Single<List<RestaurantShortDataEntityResponse>>

    @GET("filters")
    fun getFilters(): Single<List<FilterDataEntityResponse>>

    @GET("users/{user_id}/recommended-cafes")
    fun getRecommended(@Path("user_id") id: Int): Single<List<RestaurantShortDataEntityResponse>>

    @GET("users/{user_id}/favorite-cafes")
    fun getFavourite(@Path("user_id") id: Int): Single<List<RestaurantShortDataEntityResponse>>

    @GET("cafes/{id}/similar/{amount}")
    fun getSimilarPlaces(@Path("id") id: Int, @Path("amount") amount: Int):
            Single<List<RestaurantShortDataEntityResponse>>

    @GET("cafes/search/{substring}")
    fun search(@Path("substring") name: String): Single<List<RestaurantShortDataEntityResponse>>

    @POST("filtered-cafes")
    fun getFilteredPlaces(@Body filters: List<FilterDataEntityRequest>):
            Single<List<Int>>

    @POST("users/{user_id}/recommended-cafes/filtered")
    fun getFilteredRecommendedPlaces(
        @Path("user_id") id: Int,
        @Body filters: List<FilterDataEntityRequest>
    ): Single<List<Int>>

    @POST("recommended-cafes")
    fun getRecommendedUnauthorized(@Body places: List<Int>): Single<List<RestaurantShortDataEntityResponse>>

    @POST("login")
    fun login(@Body account: AccountDataEntity): Single<Int>

    @POST("register")
    fun register(@Body account: AccountDataEntity): Single<Void>
}