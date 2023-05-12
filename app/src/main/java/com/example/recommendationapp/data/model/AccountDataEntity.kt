package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.domain.model.Account
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@TypeConverters(Converters::class)
data class AccountDataEntity(
    @PrimaryKey
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
) : Parcelable {
    constructor() : this("", "")

    fun toEntity() = Account(email, password)

    companion object {
        fun fromEntity(entity: Account): AccountDataEntity = with(entity) {
            return AccountDataEntity(email, password)
        }
    }
}
