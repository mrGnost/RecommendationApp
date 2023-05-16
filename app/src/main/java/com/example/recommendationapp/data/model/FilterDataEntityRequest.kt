package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.example.recommendationapp.domain.model.Filter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterDataEntityRequest(
    @SerializedName("name") var name: String,
    @SerializedName("variants") var variants: List<String>
) : Parcelable {
    constructor() : this("", listOf())

    fun toEntity() = Filter(0, name, variants, List(variants.size) { false })

    companion object {
        fun fromEntity(entity: Filter): FilterDataEntityRequest = with(entity) {
            return FilterDataEntityRequest(name, variants.filterIndexed { index, _ -> checked[index] })
        }
    }
}