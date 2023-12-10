package com.example.gpt_assistants_interlink.presentation

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay

// Constants
val OPENAI_KEY = ""  // Hardcode your key for mobile access without a server
val GPT_MODEL = "gpt-3.5-turbo-16k"  // I recommend changing to gpt-4-1106-preview if you have access


// Assistant and Chatbot set up
fun List<Assistant>.findAssistantByName(name: String): Assistant? {
    return this.find { it.name.contains(name, ignoreCase = true) }
}

fun List<AssistantSettings>.findAssistantByName(name: String): AssistantSettings?{
    return this.find {it.name.contains(name, ignoreCase = true)}
}

data class Abilities(val interpreter: Boolean, val retrieval: Boolean)

data class AssistantSettings(
    val name: String,
    var assistant_id: String,
    val screen_color: Color,  // Color of screen when speaking
    val text_color: Color,  // Color of text when speaking (change to be readable)
    val sys_prompt: String,
    val abilities: Abilities
)

data class GPTChatModel(val code: String, val max_tokens: Int)

val assistants = listOf<AssistantSettings>(
    AssistantSettings("GAI-translator", "",
        Color(51, 204, 255),
        Color(0, 0, 0),
        TRANSLATOR_SYS_PROMPT,
        Abilities(false, false)
    ),

    AssistantSettings("GAI-generalist", "",
        Color(255, 153, 51),
        Color(0, 0, 0),
        GENERALIST_SYS_PROMPT,
        Abilities(true, true)
    ),

    AssistantSettings("GAI-engineer/mechanic", "",
        Color(153, 153, 102),
        Color(0, 0, 0),
        ENGINEER_SYS_PROMPT,
        Abilities(true, true)
    ),

    AssistantSettings("GAI-friend", "",
        Color(153, 153, 255),
        Color(0, 0, 0),
        FRIEND_SYS_PROMPT,
        Abilities(false, false)
    ),

    AssistantSettings("GAI-advisor", "",
        Color(102, 102, 153),
        Color(255, 255, 255),
        ADVISOR_SYS_PROMPT,
        Abilities(true, false)
    ),

    AssistantSettings("GAI-maths/accounting", "",
        Color(0, 102, 204),
        Color(255, 255, 255),
        MATHEMATICIAN_SYS_PROMPT,
        Abilities(true, true)
    ),

    AssistantSettings("GAI-scientist/physicist", "",
        Color(0, 153, 51),
        Color(0, 0, 0),
        SCIENTIST_SYS_PROMPT,
        Abilities(true, true)
    ),

    AssistantSettings("GAI-life_coach/psychiatrist", "",
        Color(255, 102, 153),
        Color(0, 0, 0),
        PSYCHIATRIST_SYS_PROMPT,
        Abilities(true, true)
    )
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
        Log.d("Error", "CREATE: $e")
        throw e
    } catch (e: ServerResponseException) {
        // Handle server error
        Log.d("Error", "CREATE: $e")
        throw e
    } catch (e: Exception) {
        // Handle other errors
        Log.d("Error", "CREATE: $e")
        throw e
    } finally {
        client.close()
    }
}

suspend fun list_assistants(): AssistantList{
    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
    }

    val response: AssistantList = client.get("https://api.openai.com/v1/assistants"){
        header("Content-Type", "application/json")
        header("Authorization", "Bearer $OPENAI_KEY")
        header("OpenAI-Beta", "assistants=v1")
    }
    client.close()

    return response
}

suspend fun delete_assistant(assistant_id: String): Boolean{
    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
    }

    val response: DeletedAssistantResponse = client.delete("https://api.openai.com/v1/assistants/$assistant_id"){
        header("Content-Type", "application/json")
        header("Authorization", "Bearer $OPENAI_KEY")
        header("OpenAI-Beta", "assistants=v1")
    }

    client.close()

    return response.deleted
}

suspend fun purge_assistants(context: Context){
    var failed_deletions = ""
    try{
        if (file_exists(context, "failed_deletion_list.txt")){
            failed_deletions = read_text_from_file(context, "failed_deletion_list.txt")
        }

    } catch (e: Exception){
        failed_deletions = ""
    }


    for (assistant in assistants){
        val success = delete_assistant(assistant.assistant_id)
        if (!success){
            failed_deletions = "$failed_deletions${assistant.assistant_id}\n"
        } else {
            delete_file(context, "${assistant.assistant_id}.txt")
        }
    }

    write_text_to_file(context, "failed_deletion_list.txt", failed_deletions)
}

suspend fun load_ids(context: Context){
    var ids: List<String>

    if (file_exists(context, "assistant_ids.txt")){
        ids = read_text_from_file(context, "assistant_ids.txt").split("\n")
    } else {
        throw Exception("No ids to load")
    }


    if (assistants.size != ids.size){
        throw Exception("Amount of IDs not Equal to Num Assistants")
    }

    var pos = 0  // This represents your current position in the list of assistants
    for (setting in assistants){
        setting.assistant_id = ids[pos]
        Log.d(setting.name, setting.assistant_id)
        pos++
    }

}

suspend fun check_assistants_with_user_info(assistantList: List<Assistant>): Boolean {
    // Retrieve the names of expected assistants from the constant list
    val expectedAssistantNames = assistants.map { it.name }.toSet()

    // Check if all expected assistants are present in the assistantList with "User Info:" in instructions
    return assistantList.count { assistant ->
        expectedAssistantNames.contains(assistant.name) && assistant.instructions.contains("User Info:")
    } == expectedAssistantNames.size - 1  // The translator should not have user data
}

suspend fun instantiate_or_connect_swarm(context: Context) {
    val assistant_list = list_assistants().data // This function should return a list of Assistant objects
    var id_file = "" // This is the file that will contain our assistant ids
    var userInfoText = ""

    val isGpt4PreviewModel = GPT_MODEL == "gpt-4-1106-preview"

    for (setting in assistants) { // iterate over the constant list of settings
        // 1. Check if we have an assistant already created with OpenAI
        val gai_assistant = assistant_list.find { it.name == setting.name }
        gai_assistant?.let {
            // Assistant with the given name found.
            setting.assistant_id = it.id

            if (it.instructions.contains("User Info:")) {
                userInfoText = it.instructions.substringAfter("User Info:")
                write_text_to_file(context, "userdat.txt", userInfoText, isPublic = false)
                id_file += "${it.id}\n" // Append id to file
            }
        } ?: run {
            // Assistant with the given name not found.
            val userdat = read_text_from_file(context, "userdat.txt")

            val newAssistantInstructions = if (setting.name == "GAI-translator") {  // The translator will be confused with user data
                setting.sys_prompt
            } else {
                "${setting.sys_prompt}\n\nUser Info:\n$userdat"
            }

            val codeInterpreter = setting.abilities.interpreter

            val retrieval = (isGpt4PreviewModel && setting.abilities.retrieval) // retrieval is not used unless the model is gpt-4-1106-preview and it's called for


            val new_id = create_assistant(newAssistantInstructions, setting.name, codeInterpreter, retrieval)
            setting.assistant_id = new_id
            id_file += "$new_id\n" // Append new id to file
        }
    }

    write_text_to_file(context, "assistant_ids.txt", id_file)
}


class Chatbot(val model: String, val system_prompt: String){
    // This will be a simple interface to the model
    var conversation = mutableListOf<Message>()

    init {
        conversation.add(Message("system", system_prompt))
    }

    suspend fun say_to_chatbot(content: String, tokens: Int = 250,
                                    temp: Float = 1f, role: String = "user"
    ): String {

        var conversation_list: List<Message>

        val client = HttpClient(Android) {
            install(JsonFeature) {
                serializer = JSONSERIALIZER
            }
        }

        conversation.add(Message("user", content))
        conversation_list = conversation.toList()

        val chatRequest = ChatRequest(
            model = model,
            messages = conversation_list,
            max_tokens = tokens,
            temperature = temp
        )

        val response: ChatResponse = client.post("https://api.openai.com/v1/chat/completions") {
            header("Content-Type", "application/json")
            header("Authorization", "Bearer $OPENAI_KEY")
            contentType(ContentType.Application.Json)
            body = chatRequest
        }

        client.close()
        return response.choices.first().message.content
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

    suspend fun say_to_assistant(content: String): String{
        val message_response = this.create_message(content)
        val run = this.run_thread()

        var run_state = run.status
        var ct = 0
        while (run_state != "completed" && run_state != "expired" && run_state != "failed"){
            delay(1000)
            run_state = this.run_status(run)
            ct++
        }

        val response = this.get_newest_message()
        return response
    }

    suspend fun load_or_create_thread(context: Context){
        try{
            if (file_exists(context, "$assistant_id.txt")){
                thread_id = read_text_from_file(context, "$assistant_id.txt")
            } else {
                val thread = this.create_thread()
                save_thread_to_assistant(context, assistant_id, thread_id)
            }


        } catch (e: Exception){
            Log.d("Error", e.toString())
            val thread = this.create_thread()
            save_thread_to_assistant(context, assistant_id, thread_id)
        }
    }
}

suspend fun select_assistant(prompt: String): AssistantSettings{
    val chatbot = Chatbot(GPT_MODEL, SELECTOR_SYS_PROMPT)
    var selection_made = false
    var ct = 0

    while (!selection_made && ct < 3){
        val choice = chatbot.say_to_chatbot(prompt)
        val gai_assistant = assistants.findAssistantByName(choice)

        gai_assistant?.let {  // Found something
            return gai_assistant
        } ?: run {  // Found Nothing
            ct++
        }

    }

    return assistants[1]  // Use generalist
}


suspend fun create_userdat(context: Context, prompt: String){
    val chatbot = Chatbot(GPT_MODEL, DATA_ENTRY_BOT)
    val data = chatbot.say_to_chatbot(prompt, tokens = 1089)
    write_text_to_file(context, "userdat.txt", data)
}

suspend fun select_assistant_manual(name: String): AssistantSettings{
    val gai_assistant = assistants.findAssistantByName(name)

    gai_assistant?.let {  // Found something
        return gai_assistant
    } ?: run {  // Found Nothing
        return assistants[1]
    }
}

suspend fun save_thread_to_assistant(context: Context, assistant_id: String, thread_id: String){
    write_text_to_file(context, "$assistant_id.txt", thread_id)
}


suspend fun modify_assistant(
    assistantId: String,
    model: String? = null,
    name: String? = null,
    description: String? = null,
    instructions: String? = null,
    tools: List<Map<String, String>>? = listOf(),
    fileIds: List<String>? = listOf(),
    metadata: Map<String, String>? = null
): String {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
        defaultRequest {
            header(HttpHeaders.Authorization, "Bearer $OPENAI_KEY")
            header("OpenAI-Beta", "assistants=v1")
            contentType(ContentType.Application.Json)
        }
        HttpResponseValidator {
            validateResponse { response ->
                val statusCode = response.status.value
                when (statusCode) {
                    in 400..499 -> throw ClientRequestException(response, "Client error: $statusCode")
                    in 500..599 -> throw ServerResponseException(response, "Server error: $statusCode")
                }
            }
        }
    }

    try {
        val requestBody = UpdateAssistantRequestBody(
            model = model,
            name = name,
            description = description,
            instructions = instructions,
            tools = tools,
            file_ids = fileIds,
            metadata = metadata
        )

        val response: String = client.post("https://api.openai.com/v1/assistants/$assistantId") {
            body = requestBody
        }

        return response
    } catch (e: ClientRequestException) {
        // Handle client-side request error
        e.printStackTrace()
        throw e
    } catch (e: ServerResponseException) {
        // Handle server-side error
        e.printStackTrace()
        throw e
    } catch (e: Exception) {
        // Handle any other errors
        e.printStackTrace()
        throw e
    } finally {
        client.close()
    }
}


suspend fun grab_full_assistants(saved_assistants: List<Assistant>): List<Assistant>{
    val gai_assistants = mutableListOf<Assistant>()
    //val saved_assistants_online = list_assistants().data

    for (gai in assistants){
        val located_gai = saved_assistants.findAssistantByName(gai.name)

        located_gai?.let{
            gai_assistants.add(it)

        } ?: run {
            // Don't do anything
        }
    }

    //throw Exception(gai_assistants.toString())
    return gai_assistants.toList()
}

suspend fun get_id_name_prompt(full_gai: Assistant): ID_Name_Prompt{
    return ID_Name_Prompt(full_gai.id, full_gai.name, full_gai.instructions)
}

suspend fun personalize_existing_gais(context: Context, saved_assistants: List<Assistant>){

    // 0. Ensure user data
    if (!file_exists(context, "userdat.txt")){
        throw Exception("Cannot personalize until userdat.txt filled")
    }
    val user_data = read_text_from_file(context, "userdat.txt")

    // 1. Collect GAI assistants
    val gais = saved_assistants  // Used to be something else here but just wanted a quick fix

    // 2. Collect ID_Prompts
    val gai_id_name_prompts = mutableListOf<ID_Name_Prompt>()
    var id_name_prompt: ID_Name_Prompt

    for (gai in gais){
        id_name_prompt = get_id_name_prompt(gai)
        gai_id_name_prompts.add(id_name_prompt)
    }

    // 3. Update each assistant who's prompt does not include "User Info:"
    for (gai in gai_id_name_prompts){
        if (!gai.prompt.contains("User Info:", ignoreCase = true) && gai.name != "GAI-translator"){
            // Translator should not have personalization
            modify_assistant(gai.id, instructions = "${gai.prompt}\n\nUser Info:\n$user_data")
        }
    }

}