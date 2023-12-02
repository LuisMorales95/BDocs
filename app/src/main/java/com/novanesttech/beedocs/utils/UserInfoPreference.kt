package com.novanesttech.beedocs.utils

import android.content.Context
import com.novanesttech.beedocs.BeeDocsApplication.Companion.getContext

class UserInfoPreference(
    context: Context
) {
    private val preferenceFileName = "InfoPersonal"
    private var preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
    private var editor = preferences.edit()

    fun setString(tag: String, value: String) {
        editor.putString(tag, value)
        editor.apply()
    }

    fun getString(preferenceKey: PreferenceKey): String {
        return preferences.getString(preferenceKey.name, "") ?: ""
    }

    fun hasSignedAccount(): Boolean {
        return getString(PreferenceKey.email).isNotEmpty() && getString(PreferenceKey.password).isNotEmpty()
    }

    enum class PreferenceKey(name: String) {
        email("email"),
        password("password")
    }
}