package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = DatabaseScheme.RestaurantsTableScheme.FAVOURITE_IDS)
@Parcelize
@TypeConverters(Converters::class)
data class FavouriteID(
    @PrimaryKey
    @SerializedName("id") var id: Int
) : Parcelable {
    constructor() : this(0)

    fun toEntity() = id

    companion object {
        fun fromEntity(entity: Int): FavouriteID = FavouriteID(entity)
    }
}
