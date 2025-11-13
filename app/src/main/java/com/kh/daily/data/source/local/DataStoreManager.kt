package com.kh.daily.data.source.local
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension (creates one instance per context)
val Context.dataStore by preferencesDataStore(name = "m_daily_prefs")


class DataStoreManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_AUTO_LOGIN = booleanPreferencesKey("is_auto_login")
    }

    // Save data
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
        }
    }

    suspend fun setAutoLogin(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_AUTO_LOGIN] = enabled
        }
    }

    // Read data (Flow)
    val userName: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[USER_NAME] }

    val isAutoLogin: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[IS_AUTO_LOGIN] ?: false }
}