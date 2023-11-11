package com.example.gpt_assistants_interlink.presentation

import android.util.Log
import androidx.compose.ui.graphics.Color
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

// Constants
val OPENAI_KEY = ""  // Hardcode your key for mobile access without a server
val GPT_MODEL = "gpt-3.5-turbo-16k"  // I recommend changing to gpt-4-1106-preview if you have access
val JSONSERIALIZER = GsonSerializer()


// Assistant set up
data class AssistantSettings(
    val name: String,
    val assistant_id: String,
    val screen_color: Color,  // Color of screen when speaking
    val text_color: Color  // Color of text when speaking (change to be readable)
)

val assistants = mutableMapOf<String, String>(  // In the off chance you've already got an assistant, "GAI-" should avoid conflict
    "GAI-translator" to "",  // The second part of the map is the assistant id
    "GAI-generalist" to "",  // Code interpreter and retrieval
    "GAI-engineer/mechanic" to "",  // Code interpreter and retrieval
    "GAI-selector" to "",  // Special function to determine which assistant to use depending on an initial prompt
    "GAI-friend" to "",
    "GAI-advisor" to "",
    "GAI-maths/accounting" to "",  // Gets retrieval and code interpreter
    "GAI-scientist/physicist" to "",  // code interpreter and retrieval
    "GAI-life_coach/psychiatrist" to ""
)
suspend fun create_assistant(instructions: String, name: String,
                             code_interpreter: Boolean, retrieval: Boolean): String{
    /*
    This will create an assistant with the API, preventing a coroutine function from moving
    to the next step until it's finished or fails.

    Returns assistant id
     */

    // Prepare the HTTP client

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
    }

    val tools = mutableListOf<Tool>()
    if (code_interpreter) {
        tools.add(Tool("code_interpreter"))
    }
    if (retrieval) {
        tools.add(Tool("retrieval"))
    }

    val assistantCreationRequest = AssistantCreationRequest(
        instructions = instructions,
        name = name,
        tools = tools,
        model = GPT_MODEL
    )

    try {
        val response: Assistant = client.post {
            url("https://api.openai.com/v1/assistants")
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
            body = assistantCreationRequest
        }
        return response.id
    } catch (e: ClientRequestException) {
        // Handle error appropriately by rethrowing, logging, or returning an error message
        throw e
    } catch (e: ServerResponseException) {
        // Handle server error
        throw e
    } catch (e: Exception) {
        // Handle other errors
        throw e
    } finally {
        client.close()
    }
}

suspend fun list_assistants(): List<Assistant> {
    /*
    This queries the OpenAI API for a list of your assistants.

    :return: List of Assistant
     */

    val OPENAI_BASE_URL = "https://api.openai.com/v1/assistants"
    val CONTENT_TYPE_HEADER = "Content-Type"
    val AUTHORIZATION_HEADER = "Authorization"
    val OPENAI_BETA_HEADER = "OpenAI-Beta"
    val OPENAI_BETA_VALUE = "assistants=v1"

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
        defaultRequest {
            header(CONTENT_TYPE_HEADER, "application/json")
            header(AUTHORIZATION_HEADER, "Bearer $OPENAI_KEY")
            header(OPENAI_BETA_HEADER, OPENAI_BETA_VALUE)
        }
    }

    try {
        // Perform the GET request and receive the response as a list of Assistants
        return client.get<List<Assistant>>(OPENAI_BASE_URL) {
            parameter("order", "desc")
            parameter("limit", 100)
        }
    } catch (e: Exception) {
        // Log and/or handle the error accordingly
        // For demonstration purposes, we're just printing the error
        println("Error retrieving assistants: $e")
        throw e
    } finally {
        // Close the client to free resources
        client.close()
    }
}

class Chatbot(val model: String){
    // This will be a simple interface to the model
    var conversation = mutableListOf<Message>()

    suspend fun add_message(content: String){
        // Say to chatbot, get reply. Prevent coroutine from advancing until complete or fail.
    }
}

class GPT(val assistant_id: String){
    var thread_id: String = ""

    suspend fun create_thread(): ThreadObject {
        // Add code to make a new thread from the API and return a thread object. Holds coroutine until completed or fails.

        val url = "https://api.openai.com/v1/threads"

        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        try {
            val response: ThreadObject = client.post(url) {
                header("Content-Type", "application/json")
                header("Authorization", "Bearer $OPENAI_KEY")
                header("OpenAI-Beta", "assistants=v1")
                body = ""
            }
            thread_id = response.id
            return response
        } catch (e: ClientRequestException) {
            // Handle error when server responds with client error status (4xx)
            throw e
        } catch (e: ServerResponseException) {
            // Handle error when server responds with server error status (5xx)
            throw e
        } catch (e: Exception) {
            // Handle all other exceptions
            throw e
        }
    }

    suspend fun load_thread(assistant: String): Boolean{
        /* This will load the saved thread for an assistant
        on device as ./{assistant}_thread.txt and return true,
        or return false if it cannot load. Holds coroutine
        until complete or fail.
         */

        return false
    }

    suspend fun create_message(content: String): MessageResponse {
        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        val payload = ApiMessagePayload(role = "user", content = content)
        val response: MessageResponse = client.post("https://api.openai.com/v1/threads/$thread_id/messages") {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
            contentType(ContentType.Application.Json)
            body = payload
        }

        client.close()
        return response
    }

    suspend fun run_thread(): ThreadRun{
        /*
        This calls the run function from the api on a thread,
        then periodically checks in to see the status of the
        thread run.
         */
        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        val payload = CreateRun(assistant_id)
        val response: ThreadRun = client.post("https://api.openai.com/v1/threads/$thread_id/runs"){
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
            contentType(ContentType.Application.Json)
            body = payload
        }

        client.close()
        return response
    }

    suspend fun run_status(current_run: ThreadRun): String {
        /*
        This checks on the run to determine if finished or requires another step.
         */
        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        val response: ThreadRun = client.get("https://api.openai.com/v1/threads/$thread_id/runs/${current_run.id}") {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
        }

        client.close()
        return response.status
    }


    suspend fun get_newest_message(): String{

        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        val response: MessagesListResponse = client.get("https://api.openai.com/v1/threads/$thread_id/messages?order=desc&limit=1"){
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
        }

        Log.d("Response", response.toString())
        client.close()

        val latestMessageContent = response.data.firstOrNull()?.content
        if (latestMessageContent.isNullOrEmpty()) {
            throw Exception("No content found in the latest message response.")
        }

        return latestMessageContent.first().text.value
    }

}