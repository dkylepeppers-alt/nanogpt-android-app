package com.kyle.nanogptapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kyle.nanogptapp.data.settings.SettingsGraph
import com.kyle.nanogptapp.ui.chat.ChatScreen
import com.kyle.nanogptapp.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chat", "Settings")
    val context = LocalContext.current
    val settingsRepository = remember(context) {
        SettingsGraph.repository(context)
    }
    val settings by settingsRepository.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("NanoGPT") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> ChatScreen(hasApiKey = settings.hasApiKey)
                1 -> SettingsScreen()
            }
        }
    }
}
