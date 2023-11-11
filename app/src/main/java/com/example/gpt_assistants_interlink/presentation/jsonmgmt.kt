package com.example.gpt_assistants_interlink.presentation

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class ChatRequest(val model: String, val messages: List<Message>,
                       val max_tokens: Int, val temperature: Float)

@Serializable
data class Choice(val index: Int, val message: Message, val finish_reason: String)

@Serializable
data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String?, // Add the 'model' key as a nullable property
    val choices: List<Choice>,
    val usage: Map<String, Int>
)

@Serializable
data class ThreadObject(
    val id: String,
    val `object`: String,
    val created_at: Long,
    val metadata: JsonObject // Assuming metadata can contain arbitrary JSON, use JsonObject
)

@Serializable
data class MessageResponse(
    val id: String,
    val `object`: String,
    val created_at: Long,
    val thread_id: String,
    val role: String,
    val content: List<MessageContent>,
    val file_ids: List<String>,
    val assistant_id: String,
    val run_id: String,
    val metadata: JsonObject
)

@Serializable
data class MessageContent(
    val type: String,
    val text: TextContent
)

@Serializable
data class TextContent(
    val value: String,
    val annotations: List<String> // Assuming annotations is a list of `Annotation` objects
)

@Serializable
data class ThreadRun(
    val id: String,
    val `object`: String,
    val created_at: Long,
    val assistant_id: String,
    val thread_id: String,
    val status: String,
    val started_at: Long,
    val expires_at: JsonNull? = null,
    val cancelled_at: JsonNull? = null,
    val failed_at: JsonNull? = null,
    val completed_at: Long,
    val last_error: JsonNull? = null,
    val model: String,
    val instructions: JsonNull? = null,
    val tools: List<Tool>,
    val file_ids: List<JsonNull> = emptyList(),
    val metadata: Map<String, JsonNull> = emptyMap()
)

@Serializable
data class Tool(
    val type: String
)
