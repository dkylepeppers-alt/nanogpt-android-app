package com.kyle.nanogptapp.ui.settings

import com.kyle.nanogptapp.data.settings.SettingsRepository
import com.kyle.nanogptapp.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsViewModelTest {
    @Test
    fun saveApiKey_updatesUiStateAndRepositorySnapshot() {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.onApiKeyInputChanged("secret-key")
        viewModel.saveApiKey()

        val state = viewModel.uiState.value
        assertTrue(state.settings.hasApiKey)
        assertEquals("secret-key", repository.getApiKey())
        assertEquals("API key saved securely on device", state.saveMessage)
        assertEquals("secret-key", state.apiKeyInput)
    }

    @Test
    fun clearApiKey_clearsInputAndReportsRemoval() {
        val repository = FakeSettingsRepository(initialApiKey = "secret-key")
        val viewModel = SettingsViewModel(repository)

        viewModel.clearApiKey()

        val state = viewModel.uiState.value
        assertFalse(state.settings.hasApiKey)
        assertEquals("", repository.getApiKey())
        assertEquals("", state.apiKeyInput)
        assertEquals("API key removed from secure storage", state.saveMessage)
    }

    @Test
    fun updateReasoningEnabled_updatesSettingsSnapshot() {
        val repository = FakeSettingsRepository()
        val viewModel = SettingsViewModel(repository)

        viewModel.updateReasoningEnabled(true)

        assertTrue(viewModel.uiState.value.settings.reasoningEnabled)
    }
}

private class FakeSettingsRepository(
    initialApiKey: String = "",
) : SettingsRepository {
    private var apiKey: String = initialApiKey
    private val state =
        MutableStateFlow(
            AppSettings(
                hasApiKey = initialApiKey.isNotBlank(),
            ),
        )

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override fun getApiKey(): String = apiKey

    override fun updateApiKey(apiKey: String) {
        this.apiKey = apiKey.trim()
        publish(state.value.copy(hasApiKey = this.apiKey.isNotBlank()))
    }

    override fun clearApiKey() {
        apiKey = ""
        publish(state.value.copy(hasApiKey = false))
    }

    override fun updateSelectedModel(modelId: String) {
        publish(state.value.copy(selectedModelId = modelId))
    }

    override fun updateReasoningEnabled(enabled: Boolean) {
        publish(state.value.copy(reasoningEnabled = enabled))
    }

    override fun updateSearchEnabled(enabled: Boolean) {
        publish(state.value.copy(searchEnabled = enabled))
    }

    override fun updateMemoryEnabled(enabled: Boolean) {
        publish(state.value.copy(memoryEnabled = enabled))
    }

    override fun updateMediaEnabled(enabled: Boolean) {
        publish(state.value.copy(mediaEnabled = enabled))
    }

    private fun publish(settings: AppSettings) {
        state.value = settings
    }
}
