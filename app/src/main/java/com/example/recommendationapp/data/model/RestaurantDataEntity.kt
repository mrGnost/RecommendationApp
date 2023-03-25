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

@Entity(tableName = DatabaseScheme.RestaurantsTableScheme.FULL_INFO_TABLE_NAME)
@Parcelize
@TypeConverters(Converters::class)
data class RestaurantDataEntity(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: Int,
    @SerializedName("description") var description: String,
    @SerializedName("workingHours") var workingHours: String,
    @SerializedName("contactPhoneNumbers") var contactPhoneNumbers: String,
    @SerializedName("location") var location: LocationDataEntity,
    @SerializedName("rating") var rating: Double,
    @SerializedName("priceCategory") var priceCategory: String,
    @SerializedName("categories") var categories: String,
    @SerializedName("socials") var socials: List<SocialDataEntity>?
) : Parcelable {
    constructor() : this(
        0,
        "",
        0,
        "",
        "",
        "",
        LocationDataEntity(),
        .0,
        "",
        "",
        null
    )

    fun toEntity() = Restaurant(
        id, name, photo, description, workingHours, contactPhoneNumbers,
        location.toEntity(), rating, priceCategory,
        categories, socials?.map { it.toEntity() })

    companion object {
        fun fromEntity(entity: Restaurant) = with(entity) {
            RestaurantDataEntity(id, name, photo, description, workingHours, contactPhoneNumbers,
                LocationDataEntity.fromEntity(location), rating, priceCategory,
                categories, socials?.map { SocialDataEntity.fromEntity(it) })
        }
    }
}
