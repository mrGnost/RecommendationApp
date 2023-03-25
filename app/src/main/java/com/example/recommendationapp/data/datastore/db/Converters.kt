package com.example.recommendationapp.data.datastore.db

import androidx.room.TypeConverter
import com.example.recommendationapp.data.model.LocationDataEntity
import com.example.recommendationapp.data.model.SocialDataEntity
import com.google.gson.Gson

object Converters {
    val gson = Gson()

    @TypeConverter
    fun socialsToJson(value: List<SocialDataEntity>) = gson.toJson(value)

    @TypeConverter
    fun jsonToSocials(value: String) = gson.fromJson(value, Array<SocialDataEntity>::class.java).toList()

    @TypeConverter
    fun locationToJson(value: LocationDataEntity) = gson.toJson(value)

    @TypeConverter
    fun jsonToLocation(value: String) = gson.fromJson(value, LocationDataEntity::class.java)
}