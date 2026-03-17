package com.kyle.nanogptapp.model

/**
 * Kotlin-side models for the app architecture sketch.
 *
 * These are intentionally simple placeholders for the first pass.
 */
data class ChatMessage(
    val role: String,
    val content: String,
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val reasoningEnabled: Boolean = false,
    val reasoningEffort: String = "low",
    val stream: Boolean = false,
)

data class ModelSummary(
    val id: String,
    val displayName: String = id,
    val supportsVision: Boolean = false,
    val supportsReasoning: Boolean = false,
)
