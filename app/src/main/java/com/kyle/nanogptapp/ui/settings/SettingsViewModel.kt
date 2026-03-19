package com.kyle.nanogptapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyle.nanogptapp.data.api.NanoGptApiClient
import com.kyle.nanogptapp.data.settings.SettingsRepository
import com.kyle.nanogptapp.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val apiKeyInput: String = "",
    val isApiKeyVisible: Boolean = false,
    val saveMessage: String? = null,
    val isVerifyingKey: Boolean = false,
)

class SettingsViewModel(
    private val repository: SettingsRepository,
) : ViewModel() {
    val settings = repository.settings

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            settings = repository.settings.value,
            apiKeyInput = repository.getApiKey(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(settings = repository.settings.value) }
    }

    fun onApiKeyInputChanged(value: String) {
        _uiState.update { it.copy(apiKeyInput = value, saveMessage = null) }
    }

    fun onApiKeyVisibilityChanged(isVisible: Boolean) {
        _uiState.update { it.copy(isApiKeyVisible = isVisible) }
    }

    fun saveApiKey() {
        repository.updateApiKey(_uiState.value.apiKeyInput)
        _uiState.update {
            it.copy(
                settings = repository.settings.value,
                saveMessage = if (repository.settings.value.hasApiKey) {
                    "API key saved securely on device"
                } else {
                    "API key cleared"
                },
            )
        }
    }

    fun clearApiKey() {
        repository.clearApiKey()
        _uiState.update {
            it.copy(
                settings = repository.settings.value,
                apiKeyInput = "",
                saveMessage = "API key removed from secure storage",
            )
        }
    }

    fun verifyApiKey() {
        val key = _uiState.value.apiKeyInput.trim()
        if (key.isBlank()) {
            _uiState.update { it.copy(saveMessage = "Enter an API key first") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifyingKey = true, saveMessage = null) }
            val result = NanoGptApiClient(key).fetchModels()
            _uiState.update { state ->
                state.copy(
                    isVerifyingKey = false,
                    saveMessage = if (result.isSuccess) {
                        "Token valid — ${result.getOrDefault(emptyList()).size} models available"
                    } else {
                        "Verification failed: ${result.exceptionOrNull()?.message}"
                    },
                )
            }
        }
    }

    fun updateSelectedModel(modelId: String) {
        repository.updateSelectedModel(modelId)
        syncSettings()
    }

    fun updateReasoningEnabled(enabled: Boolean) {
        repository.updateReasoningEnabled(enabled)
        syncSettings()
    }

    fun updateSearchEnabled(enabled: Boolean) {
        repository.updateSearchEnabled(enabled)
        syncSettings()
    }

    fun updateMemoryEnabled(enabled: Boolean) {
        repository.updateMemoryEnabled(enabled)
        syncSettings()
    }

    fun updateMediaEnabled(enabled: Boolean) {
        repository.updateMediaEnabled(enabled)
        syncSettings()
    }

    private fun syncSettings() {
        _uiState.update { it.copy(settings = repository.settings.value) }
    }
}
