package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.example.recommendationapp.domain.model.Location
import com.example.recommendationapp.domain.model.RestaurantShort
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = DatabaseScheme.RestaurantsTableScheme.SHORT_INFO_TABLE_NAME)
@Parcelize
@TypeConverters(Converters::class)
data class RestaurantShortDataEntity(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: Int,
    @SerializedName("address") var address: String,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("categories") var categories: String,
    @SerializedName("favourite") var favourite: Boolean?
) : Parcelable {
    constructor() : this(0, "", 0, "", .0, .0, "", null)

    fun toEntity() = RestaurantShort(id, name, photo, address,
        Location(latitude, longitude), categories, favourite)

    companion object {
        fun fromEntity(entity: RestaurantShort) = with(entity) {
            RestaurantShortDataEntity(id, name, photo, address,
                location.latitude, location.longitude, categories, favourite)
        }
    }
}
