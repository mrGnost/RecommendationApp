package com.example.recommendationapp.data.api

import com.example.recommendationapp.data.model.*
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface RecommendationApi {
    @GET("cafes/{cafe_id}")
    fun getRestaurantInfo(@Path("cafe_id") id: Int): Single<RestaurantDataEntity>

    @GET("cafes")
    fun getAllRestaurants(): Single<List<RestaurantShortDataEntityResponse>>

    @GET("filters")
    fun getFilters(): Single<List<FilterDataEntityResponse>>

    @GET("recommended-cafes")
    fun getRecommended(@Header("Authorization") token: String): Single<List<RestaurantShortDataEntityResponse>>

    @GET("favorite-cafes")
    fun getFavourite(@Header("Authorization") token: String): Single<List<RestaurantShortDataEntityResponse>>

    @GET("bookmarks")
    fun getMarked(@Header("Authorization") token: String): Single<List<RestaurantShortDataEntityResponse>>

    @GET("cafes/{id}/similar/{amount}")
    fun getSimilarPlaces(@Path("id") id: Int, @Path("amount") amount: Int):
            Single<List<RestaurantShortDataEntityResponse>>

    @GET("cafes/search/{substring}")
    fun search(@Path("substring") name: String): Single<List<RestaurantShortDataEntityResponse>>

    @POST("filtered-cafes")
    fun getFilteredPlaces(@Body filters: List<FilterDataEntityRequest>):
            Single<List<Int>>

    @POST("recommended-cafes/filtered")
    fun getFilteredRecommendedPlaces(
        @Header("Authorization") token: String,
        @Body filters: List<FilterDataEntityRequest>
    ): Single<List<Int>>

    @POST("recommended-cafes-for")
    fun getRecommendedUnauthorized(@Body places: List<Int>): Single<List<RestaurantShortDataEntityResponse>>

    @POST("login")
    fun login(@Body account: AccountDataEntity): Single<TokenDataEntity>

    @POST("register")
    fun register(@Body account: AccountDataEntity): Completable

    @PUT("add-favorite-cafes")
    fun addFavourites(@Header("Authorization") token: String, @Body cafes: List<Int>): Completable

    @PUT("add-bookmarks")
    fun addMarked(@Header("Authorization") token: String, @Body cafes: List<Int>): Completable

    @DELETE("remove-favorite-cafes")
    fun removeFavourites(@Header("Authorization") token: String, @Body cafes: List<Int>): Completable

    @DELETE("remove-bookmarks")
    fun removeMarked(@Header("Authorization") token: String, @Body cafes: List<Int>): Completable
}