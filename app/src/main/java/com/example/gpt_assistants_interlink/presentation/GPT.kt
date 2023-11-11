package com.example.gpt_assistants_interlink.presentation

import androidx.compose.runtime.Composable
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// Constants
val OPENAI_KEY = ""  // Hardcode your key for mobile access without a server
val GPT_MODEL = "gpt-3.5-turbo-16k"  // I recommend changing to gpt-4-1106-preview if you have access


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
    val jsonSerializer = KotlinxSerializer(Json {
        ignoreUnknownKeys = true
        // other configuration if needed
    })

    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = jsonSerializer
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

suspend fun list_assistants(): List<Assistant>{
    /*
    This will query the OpenAI API for your assistants and return them as a list.
    Holds coroutine until complete or fail.
     */
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

    suspend fun create_thread(): ThreadObject{
        // Add code to make a new thread from api. Holds coroutine until completed or fail.

        return ThreadObject() // This will be an error until the function is filled out lol
    }

    suspend fun load_thread(assistant: String): Boolean{
        /* This will load the saved thread for an assistant
        on device as ./{assistant}_thread.txt and return true,
        or return false if it cannot load. Holds coroutine
        until complete or fail.
         */

        return false
    }

    suspend fun create_message(content: String): MessageResponse{

        /*
        This creates a message from the API. Holds coroutine until complete or fail.
         */

        return MessageResponse()
    }

    suspend fun run_thread(): ThreadObject{

        /*
        This calls the run function from the api on a thread,
        then periodically checks in to see the status of the
        thread run.
         */

        return ThreadObject()
    }
}