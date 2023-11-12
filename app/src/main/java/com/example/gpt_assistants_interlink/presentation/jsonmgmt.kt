package com.example.gpt_assistants_interlink.presentation

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
)

@Serializable
data class MessagesListResponse(
    @SerializedName("object") val object_type: String,
    val data: List<MessageResponse>,
    val first_id: String,
    val last_id: String,
    val has_more: Boolean
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
    val metadata: Map<String, Any> // Using a map to represent JSON object
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
    val completed_at: Long,
    val model: String,
    val tools: List<Tool>,
)

@Serializable
data class AssistantList(
    val `object`: String,
    val data: List<Assistant>
)

@Serializable
data class DeletedAssistantResponse(
    val id: String,
    val `object`: String,
    val deleted: Boolean
)

@Serializable
data class Assistant(
    val id: String,
    val `object`: String,
    val created_at: Long,
    val name: String,
    val description: String?,
    val model: String,
    val instructions: String,
    val tools: List<Tool>,
    val file_ids: List<String>
)

@Serializable
data class Tool(
    val type: String
)

@Serializable
data class Step(
    val id: String,
    val `object`: String,
    val created_at: Long,
    val run_id: String,
    val assistant_id: String,
    val thread_id: String,
    val type: String,
    val status: String,
    val cancelled_at: Long?,
    val completed_at: Long?,
    val expired_at: Long?,
    val failed_at: Long?,
    val last_error: String?,
    val step_details: StepDetails
)

@Serializable
data class StepDetails(
    val type: String,
    val message_creation: MessageCreation?
)

@Serializable
data class MessageCreation(
    val message_id: String
)

@Serializable
data class AssistantCreationRequest(
    val instructions: String,
    val name: String,
    val tools: List<Tool>,
    val model: String
)

@Serializable
data class ApiMessagePayload(val role: String, val content: String)

@Serializable
data class CreateRun(val assistant_id: String)

@Serializable
data class PollRun(val run_id: String)

