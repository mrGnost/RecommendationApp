package com.example.recommendationapp.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recommendationapp.data.datastore.db.Converters
import com.example.recommendationapp.domain.model.Account
import com.example.recommendationapp.domain.model.AccountLocal
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@TypeConverters(Converters::class)
data class AccountLocalDataEntity(
    @PrimaryKey
    @SerializedName("email") var email: String,
    @SerializedName("token") var token: String,
) : Parcelable {
    constructor() : this("", "")

    fun toEntity() = AccountLocal(email, token)

    companion object {
        fun fromEntity(entity: AccountLocal): AccountLocalDataEntity = with(entity) {
            return AccountLocalDataEntity(email, token)
        }
    }
}
