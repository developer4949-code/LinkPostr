package com.linkpostr.app.data.ai

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    val temperature: Double,
    val stream: Boolean = false,
)

data class ChatMessage(
    val role: String,
    val content: String,
)

data class ChatCompletionResponse(
    val choices: List<ChatChoice> = emptyList(),
)

data class ChatChoice(
    val message: ChatMessageContent,
)

data class ChatMessageContent(
    val role: String? = null,
    val content: String? = null,
)

