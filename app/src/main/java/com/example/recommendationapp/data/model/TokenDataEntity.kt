package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.domain.model.Token
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@TypeConverters(Converters::class)
data class TokenDataEntity(
    @PrimaryKey
    @SerializedName("token") var token: String
) : Parcelable {
    constructor() : this("")

    fun toEntity() = Token(token)

    companion object {
        fun fromEntity(entity: Token) = with(entity) {
            Token(token)
        }
    }
}