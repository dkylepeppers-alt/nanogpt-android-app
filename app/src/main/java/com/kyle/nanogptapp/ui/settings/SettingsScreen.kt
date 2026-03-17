package com.kyle.nanogptapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyle.nanogptapp.data.settings.SettingsGraph

@Composable
fun SettingsScreen() {
    val context = LocalContext.current.applicationContext
    val factory = remember(context) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(SettingsGraph.repository(context)) as T
            }
        }
    }
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onApiKeyInputChanged = viewModel::onApiKeyInputChanged,
        onApiKeyVisibilityChanged = viewModel::onApiKeyVisibilityChanged,
        onSaveApiKey = viewModel::saveApiKey,
        onClearApiKey = viewModel::clearApiKey,
        onReasoningChanged = viewModel::updateReasoningEnabled,
        onSearchChanged = viewModel::updateSearchEnabled,
        onMemoryChanged = viewModel::updateMemoryEnabled,
        onMediaChanged = viewModel::updateMediaEnabled,
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onApiKeyInputChanged: (String) -> Unit,
    onApiKeyVisibilityChanged: (Boolean) -> Unit,
    onSaveApiKey: () -> Unit,
    onClearApiKey: () -> Unit,
    onReasoningChanged: (Boolean) -> Unit,
    onSearchChanged: (Boolean) -> Unit,
    onMemoryChanged: (Boolean) -> Unit,
    onMediaChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Secure credentials")
                Text(
                    if (uiState.settings.hasApiKey) {
                        "A NanoGPT API key is stored in encrypted on-device preferences."
                    } else {
                        "No API key saved yet. Add one before wiring up live API calls."
                    },
                )

                OutlinedTextField(
                    value = uiState.apiKeyInput,
                    onValueChange = onApiKeyInputChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("NanoGPT API key") },
                    singleLine = true,
                    visualTransformation = if (uiState.isApiKeyVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                    ),
                    supportingText = {
                        Text(
                            "Stored locally only. Future network clients can read it through the settings repository.",
                        )
                    },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = { onApiKeyVisibilityChanged(!uiState.isApiKeyVisible) }) {
                        Text(if (uiState.isApiKeyVisible) "Hide" else "Show")
                    }
                    Button(onClick = onSaveApiKey) {
                        Text("Save key")
                    }
                    OutlinedButton(onClick = onClearApiKey) {
                        Text("Clear")
                    }
                }

                uiState.saveMessage?.let { Text(it) }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Feature toggles")
                SettingsToggle(
                    label = "Reasoning",
                    checked = uiState.settings.reasoningEnabled,
                    onCheckedChange = onReasoningChanged,
                )
                SettingsToggle(
                    label = "Web search (:online)",
                    checked = uiState.settings.searchEnabled,
                    onCheckedChange = onSearchChanged,
                )
                SettingsToggle(
                    label = "Memory (:memory)",
                    checked = uiState.settings.memoryEnabled,
                    onCheckedChange = onMemoryChanged,
                )
                SettingsToggle(
                    label = "Future media generation path",
                    checked = uiState.settings.mediaEnabled,
                    onCheckedChange = onMediaChanged,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Settings architecture")
                Text("This pass keeps secure credentials separate from general app preferences.")
                Text(
                    "That leaves room for future model selection, image/video defaults, render settings, and account-level toggles without rewriting the storage layer.",
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
