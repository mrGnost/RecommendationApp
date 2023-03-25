package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.example.recommendationapp.domain.model.Social
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@TypeConverters(Converters::class)
data class SocialDataEntity(
    @SerializedName("type") var type: String,
    @PrimaryKey
    @SerializedName("link") var link: String
) : Parcelable {
    constructor() : this("", "")

    fun toEntity() = Social(type, link)

    companion object {
        fun fromEntity(entity: Social) = with(entity) {
            SocialDataEntity(type, link)
        }
    }
}