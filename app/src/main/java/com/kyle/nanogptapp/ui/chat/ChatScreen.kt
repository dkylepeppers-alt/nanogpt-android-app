package com.kyle.nanogptapp.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

object ChatScreenTags {
    const val STATUS_CARD = "chat_status_card"
    const val MESSAGE_INPUT = "chat_message_input"
    const val SEND_BUTTON = "chat_send_button"
    const val CLEAR_BUTTON = "chat_clear_button"
}

internal fun chatStatusMessage(hasApiKey: Boolean): String {
    return if (hasApiKey) {
        "Ready for local chat input. Live requests stay disabled until networking is wired."
    } else {
        "No API key configured yet. Use Settings to save one before enabling live requests."
    }
}

@Composable
fun ChatScreen(hasApiKey: Boolean) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(ChatScreenTags.STATUS_CARD),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Chat milestone")
                Text("This screen is the first target: text chat, model selection, then streaming.")
                Text("The architecture is being kept open for future image and video generation flows.")
                Text(chatStatusMessage(hasApiKey))
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(ChatScreenTags.MESSAGE_INPUT),
            label = { Text("Message") },
            enabled = hasApiKey,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { },
                enabled = hasApiKey,
                modifier = Modifier.testTag(ChatScreenTags.SEND_BUTTON),
            ) {
                Text("Send")
            }
            Button(
                onClick = { input = "" },
                modifier = Modifier.testTag(ChatScreenTags.CLEAR_BUTTON),
            ) {
                Text("Clear")
            }
        }
    }
}
