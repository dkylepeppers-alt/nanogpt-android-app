package com.kyle.nanogptapp.model

/**
 * App-wide settings snapshot.
 *
 * Kept feature-neutral so chat, image, and video settings can live here over time
 * without the settings layer becoming chat-specific.
 */
data class AppSettings(
    val hasApiKey: Boolean = false,
    val selectedModelId: String = "",
    val reasoningEnabled: Boolean = false,
    val searchEnabled: Boolean = false,
    val memoryEnabled: Boolean = false,
    val mediaEnabled: Boolean = false
)
