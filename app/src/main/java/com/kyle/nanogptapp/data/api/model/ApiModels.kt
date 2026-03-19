package com.kyle.nanogptapp.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Top-level envelope returned by GET /v1/models. */
@Serializable
data class ModelsResponse(
    @SerialName("object") val objectType: String = "",
    val data: List<ApiModel> = emptyList(),
)

/** One model entry from the NanoGPT models list. */
@Serializable
data class ApiModel(
    val id: String,
    @SerialName("object") val objectType: String = "",
    val created: Long = 0L,
    @SerialName("owned_by") val ownedBy: String = "",
)
