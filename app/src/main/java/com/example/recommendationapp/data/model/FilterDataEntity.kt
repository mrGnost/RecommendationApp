package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.data.datastore.db.DatabaseScheme
import com.example.recommendationapp.domain.model.Filter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = DatabaseScheme.RestaurantsTableScheme.FILTERS_TABLE_NAME)
@Parcelize
@TypeConverters(Converters::class)
data class FilterDataEntity(
    @PrimaryKey
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("variants") var variants: List<String>,
    @SerializedName("checked") var checked: MutableList<Boolean>
) : Parcelable {
    constructor() : this(0, "", listOf(), mutableListOf())

    fun toEntity() = Filter(id, name, variants, checked)

    companion object {
        fun fromEntity(entity: Filter): FilterDataEntity = with(entity) {
            return FilterDataEntity(id, name, variants, checked.toMutableList())
        }
    }
}