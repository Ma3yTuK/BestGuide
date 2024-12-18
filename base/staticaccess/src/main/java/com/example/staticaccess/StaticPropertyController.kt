package com.example.staticaccess

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")
private const val TOKEN_PROPERTY_NAME = "TOKEN"

class StaticPropertyController(private val context: Context) {
    suspend fun readToken(): String? {
        val propertyName = stringPreferencesKey(TOKEN_PROPERTY_NAME)
        return context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[propertyName]
            }
            .first()
    }
    suspend fun writeToken(value: String) {
        val propertyName = stringPreferencesKey(TOKEN_PROPERTY_NAME)
        context.dataStore.edit { settings ->
            settings[propertyName] = value
        }
    }
    suspend fun deleteToken() {
        val propertyName = stringPreferencesKey(TOKEN_PROPERTY_NAME)
        context.dataStore.edit { settings ->
            settings.remove(propertyName)
        }
    }
}