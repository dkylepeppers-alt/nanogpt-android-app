package com.kyle.nanogptapp.data.api

/**
 * Implementation notes for the eventual NanoGPT API client.
 *
 * Real network code should:
 * - set Authorization: Bearer <apiKey>
 * - use base URL https://nano-gpt.com/api
 * - query /v1/models or /api/v1/models?detailed=true as documented
 * - support SSE streaming for chat completions
 */
object NanoGptApiNotes {
    const val BASE_URL = "https://nano-gpt.com/api"
    const val MODELS_PATH = "/v1/models"
    const val DETAILED_MODELS_PATH = "/api/v1/models?detailed=true"
}
