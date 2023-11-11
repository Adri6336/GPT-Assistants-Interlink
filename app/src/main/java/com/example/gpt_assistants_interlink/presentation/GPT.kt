package com.example.gpt_assistants_interlink.presentation

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

val OPENAI_KEY = ""  // Hardcode your key for mobile access without a server
val GPT_MODEL = "gpt-3.5-turbo-16k"  // I recommend changing to gpt-4-1106-preview if you have access

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
                             code_interpreter: Boolean, retrieval: Boolean,
                             stateTracker: StateTracker){
    /*
    This will create an assistant with the API, preventing a coroutine function from moving
    to the next step until it's finished or fails.
     */
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