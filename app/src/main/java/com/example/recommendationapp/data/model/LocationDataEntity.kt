package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.domain.model.Location
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@TypeConverters(Converters::class)
data class LocationDataEntity(
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double
) : Parcelable {
    constructor() : this(.0, .0)

    fun toEntity(): Location = Location(latitude, longitude)

    companion object {
        fun fromEntity(entity: Location): LocationDataEntity = with(entity) {
            LocationDataEntity(latitude, longitude)
        }
    }
}