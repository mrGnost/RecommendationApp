package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.example.recommendationapp.domain.model.Location
import com.example.recommendationapp.domain.model.Restaurant
import com.example.recommendationapp.domain.model.Social
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@TypeConverters(Converters::class)
data class RestaurantDataEntity(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: Int,
    @SerializedName("dishPhotos") var dishPhotos: List<Int>,
    @SerializedName("description") var description: String,
    @SerializedName("workingHours") var workingHours: String,
    @SerializedName("address") var address: String,
    @SerializedName("contactPhoneNumbers") var contactPhoneNumbers: String,
    @SerializedName("location") var location: LocationDataEntity,
    @SerializedName("rating") var rating: Double,
    @SerializedName("categories") var categories: String,
    @SerializedName("tags") var tags: String,
    @SerializedName("socials") var socials: List<SocialDataEntity>,
    @SerializedName("chainCafes") var chainCafes: List<RestaurantShortDataEntity>
) : Parcelable {
    constructor() : this(
        0,
        "",
        0,
        listOf(),
        "",
        "",
        "",
        "",
        LocationDataEntity(),
        .0,
        "",
        "",
        listOf(),
        listOf()
    )

    fun toEntity() = Restaurant(
        id, name, photo, dishPhotos, description, workingHours,
        address, contactPhoneNumbers, location.toEntity(), rating,
        categories, tags, socials.map { it.toEntity() }, chainCafes.map { it.toEntity() })

    companion object {
        fun fromEntity(entity: Restaurant) = with(entity) {
            RestaurantDataEntity(
                id, name, photo, dishPhotos, description, workingHours,
                address, contactPhoneNumbers, LocationDataEntity.fromEntity(location), rating,
                categories, tags, socials.map { SocialDataEntity.fromEntity(it) },
                chainCafes.map { RestaurantShortDataEntity.fromEntity(it) })
        }
    }
}
