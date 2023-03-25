package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.example.recommendationapp.domain.model.Filter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterDataEntity(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("variants") var variants: List<String>
) : Parcelable {
    constructor() : this(0, "", listOf())

    fun toEntity() = Filter(id, name, variants)

    companion object {
        fun fromEntity(entity: Filter): FilterDataEntity = with(entity) {
            return FilterDataEntity(id, name, variants)
        }
    }
}