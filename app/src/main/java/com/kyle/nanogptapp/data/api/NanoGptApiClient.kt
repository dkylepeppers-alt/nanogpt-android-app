package com.kyle.nanogptapp.data.api

import com.kyle.nanogptapp.data.api.model.ApiModel
import com.kyle.nanogptapp.data.api.model.ModelsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * HTTP client for the NanoGPT API.
 *
 * Every request includes `Authorization: Bearer <apiKey>` as required by the API.
 * Network calls are dispatched on [Dispatchers.IO]; callers may call suspend functions
 * from any coroutine context.
 */
class NanoGptApiClient(private val apiKey: String) {

    private val httpClient = OkHttpClient.Builder().build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Fetches the list of available models from [NanoGptApiNotes.MODELS_PATH].
     *
     * @return [Result.success] with the model list, or [Result.failure] with an [IOException].
     */
    suspend fun fetchModels(): Result<List<ApiModel>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${NanoGptApiNotes.BASE_URL}${NanoGptApiNotes.MODELS_PATH}")
            .header("Authorization", "Bearer $apiKey")
            .build()
        try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                    ?: return@withContext Result.failure(IOException("Empty response body"))
                val modelsResponse = json.decodeFromString<ModelsResponse>(body)
                Result.success(modelsResponse.data)
            } else {
                Result.failure(IOException("API error: HTTP ${response.code}"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
