package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.example.recommendationapp.domain.model.RestaurantShort
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class RestaurantShortDataEntityResponse(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: Int,
    @SerializedName("address") var address: String,
    @SerializedName("location") var location: LocationDataEntity,
    @SerializedName("categories") var categories: String
) : Parcelable {
    constructor() : this(0, "", 0, "", LocationDataEntity(), "")

    fun toEntity() = RestaurantShort(id, name, photo, address,
        location.toEntity(), categories
    )

    companion object {
        fun fromEntity(entity: RestaurantShort) = with(entity) {
            RestaurantShortDataEntityResponse(id, name, photo, address,
                LocationDataEntity.fromEntity(location), categories)
        }
    }
}