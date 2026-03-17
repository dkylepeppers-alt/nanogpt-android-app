package com.kyle.nanogptapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var apiKey by remember { mutableStateOf("") }
    var reasoningEnabled by remember { mutableStateOf(false) }
    var searchEnabled by remember { mutableStateOf(false) }
    var memoryEnabled by remember { mutableStateOf(false) }
    var mediaEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("NanoGPT API key") }
        )

        SettingToggle("Reasoning", reasoningEnabled) { reasoningEnabled = it }
        SettingToggle("Web search (:online)", searchEnabled) { searchEnabled = it }
        SettingToggle("Memory (:memory)", memoryEnabled) { memoryEnabled = it }
        SettingToggle("Future media generation path", mediaEnabled) { mediaEnabled = it }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Media roadmap")
                Text("The app is being structured so image and video generation can later be added as separate features, not hacks bolted onto chat.")
                Text("Planned future areas: generation jobs, gallery/history, prompt presets, upload/reference inputs, and render status tracking.")
            }
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
