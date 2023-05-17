package com.example.recommendationapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.recommendationapp.data.model.AccountLocalDataEntity
import com.example.recommendationapp.utils.Common.PREFERENCE_NAME
import com.example.recommendationapp.utils.Common.PREF_ACCOUNT_NAME
import com.example.recommendationapp.utils.Common.PREF_ACCOUNT_TOKEN
import com.example.recommendationapp.utils.Common.PREF_ONBOARDING_VIEWED

class LocalDataSourceImpl(val context: Context) : LocalDataSource {
    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    override fun setAccount(account: AccountLocalDataEntity) {
        editor.putString(PREF_ACCOUNT_NAME, account.email)
        editor.putString(PREF_ACCOUNT_TOKEN, account.token)
        editor.commit()
    }

    override fun getAccount(): AccountLocalDataEntity {
        val email = pref.getString(PREF_ACCOUNT_NAME, "")!!
        val token = pref.getString(PREF_ACCOUNT_TOKEN, "")!!
        return AccountLocalDataEntity(email, token)
    }

    override fun clearAccount() {
        editor.putString(PREF_ACCOUNT_NAME, "")
        editor.putString(PREF_ACCOUNT_TOKEN, "")
        editor.commit()
    }

    override fun setOnboardingViewed() {
        editor.putBoolean(PREF_ONBOARDING_VIEWED, true)
        editor.commit()
    }

    override fun checkOnboardingViewed(): Boolean = pref.getBoolean(PREF_ONBOARDING_VIEWED, false)
}