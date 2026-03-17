package com.kyle.nanogptapp.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kyle.nanogptapp.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val appContext = context.applicationContext

    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        SECURE_PREFS_NAME,
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun getApiKey(): String = securePrefs.getString(KEY_API_KEY, "") ?: ""

    fun updateApiKey(apiKey: String) {
        securePrefs.edit().putString(KEY_API_KEY, apiKey.trim()).apply()
        publish()
    }

    fun clearApiKey() {
        securePrefs.edit().remove(KEY_API_KEY).apply()
        publish()
    }

    fun updateSelectedModel(modelId: String) {
        prefs.edit().putString(KEY_SELECTED_MODEL_ID, modelId).apply()
        publish()
    }

    fun updateReasoningEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REASONING_ENABLED, enabled).apply()
        publish()
    }

    fun updateSearchEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SEARCH_ENABLED, enabled).apply()
        publish()
    }

    fun updateMemoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MEMORY_ENABLED, enabled).apply()
        publish()
    }

    fun updateMediaEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MEDIA_ENABLED, enabled).apply()
        publish()
    }

    private fun publish() {
        _settings.value = readSettings()
    }

    private fun readSettings(): AppSettings {
        val apiKey = getApiKey()
        return AppSettings(
            hasApiKey = apiKey.isNotBlank(),
            selectedModelId = prefs.getString(KEY_SELECTED_MODEL_ID, "") ?: "",
            reasoningEnabled = prefs.getBoolean(KEY_REASONING_ENABLED, false),
            searchEnabled = prefs.getBoolean(KEY_SEARCH_ENABLED, false),
            memoryEnabled = prefs.getBoolean(KEY_MEMORY_ENABLED, false),
            mediaEnabled = prefs.getBoolean(KEY_MEDIA_ENABLED, false)
        )
    }

    private companion object {
        const val PREFS_NAME = "nanogpt_settings"
        const val SECURE_PREFS_NAME = "nanogpt_secure_settings"

        const val KEY_API_KEY = "api_key"
        const val KEY_SELECTED_MODEL_ID = "selected_model_id"
        const val KEY_REASONING_ENABLED = "reasoning_enabled"
        const val KEY_SEARCH_ENABLED = "search_enabled"
        const val KEY_MEMORY_ENABLED = "memory_enabled"
        const val KEY_MEDIA_ENABLED = "media_enabled"
    }
}
