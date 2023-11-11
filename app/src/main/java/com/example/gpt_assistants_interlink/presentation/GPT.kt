package com.example.gpt_assistants_interlink.presentation

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

val OPENAI_KEY = ""  // Hardcode your key for mobile access without a server

val assistants = mapOf(
    "translator" to "",  // The second part of the map is the assistant id
    "generalist" to "",  // Code interpreter and retrieval
    "auto_mechanic" to "",  // Code interpreter and retrieval
    "psychiatrist" to "",  // Retrieval
    "professional" to "",  // Code interpreter
    "selector" to "",  // Special function
    "friend" to "",
    "advisor" to "",
    "maths/accounting" to "",  // Gets retrieval and code interpreter
    "scientist/physicist" to "",  // code interpreter and retrieval
    "life_coach" to ""
)


class Chatbot(val model: String){
    // This will be a simple interface to the model
    var conversation = mutableListOf<Message>()

    suspend fun add_message(content: String){
        // Say to chatbot, get reply
    }
}

class GPT(val assistant_id: String){
    var thread_id: String = ""

    suspend fun create_thread(): ThreadObject{
        // Add code to get thread from api

        return ThreadObject()
    }

    suspend fun load_thread(): Boolean{
        // This will load the saved thread for an assistant
        return false
    }

    suspend fun create_message(content: String): MessageResponse{

        return MessageResponse()
    }

    suspend fun run_thread(): ThreadObject{

        return ThreadObject()
    }
}