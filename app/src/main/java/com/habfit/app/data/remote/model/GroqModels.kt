package com.habfit.app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqRequest(
    @SerialName("model") val model: String,
    @SerialName("messages") val messages: List<GroqMessage>,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("stream") val stream: Boolean? = false
)

@Serializable
data class GroqMessage(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String
)

@Serializable
data class GroqResponse(
    @SerialName("id") val id: String,
    @SerialName("object") val obj: String,
    @SerialName("created") val created: Long,
    @SerialName("model") val model: String,
    @SerialName("choices") val choices: List<GroqChoice>,
    @SerialName("usage") val usage: GroqUsage
)

@Serializable
data class GroqChoice(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: GroqMessage,
    @SerialName("finish_reason") val finishReason: String
)

@Serializable
data class GroqUsage(
    @SerialName("prompt_tokens") val promptTokens: Int,
    @SerialName("completion_tokens") val completionTokens: Int,
    @SerialName("total_tokens") val totalTokens: Int
)
