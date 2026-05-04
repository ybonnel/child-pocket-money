package com.example.pocketmoney.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class AppTheme { SYSTEM, LIGHT, DARK }

data class UserPreferences(
    val currencyCode: String = "EUR",
    val theme: AppTheme = AppTheme.SYSTEM,
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val CURRENCY_KEY = stringPreferencesKey("currency_code")
    private val THEME_KEY = stringPreferencesKey("theme")

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            currencyCode = prefs[CURRENCY_KEY] ?: "EUR",
            theme = prefs[THEME_KEY]?.let {
                runCatching { AppTheme.valueOf(it) }.getOrNull()
            } ?: AppTheme.SYSTEM,
        )
    }

    suspend fun setCurrency(currencyCode: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENCY_KEY] = currencyCode
        }
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}
