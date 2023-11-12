package com.example.gpt_assistants_interlink.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.wear.tiles.material.ButtonColors
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

// This file is dedicated to the processing of language, and the speaking of it
val openai_voice = "nova"
private lateinit var textToSpeech: TextToSpeech


// Speech to Text Functions
// Transferred from old project because it works. They are probably not pretty.

suspend fun moderate(content: String): Boolean {
    val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = JSONSERIALIZER
        }
    }

    val response: ModerationResponse = client.post("https://api.openai.com/v1/moderations") {
        header("Content-Type", "application/json")
        header("Authorization", "Bearer $OPENAI_KEY")
        contentType(ContentType.Application.Json)
        body = ModerationPayload(content)
    }

    client.close()
    return response.results.first().flagged
}

suspend fun recordAudioAndTranscribe(context: Context,
                                     stop: MutableState<Boolean>,
                                     textContent: MutableState<String>): String? {

    textContent.value = "Listening ..."

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    val audioFileName = "${context.externalCacheDir?.absolutePath}/audio_${UUID.randomUUID()}.m4a"
    val mediaRecorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(audioFileName)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    }

    try {
        mediaRecorder.prepare()
    } catch (e: IOException) {
        return null
    }

    mediaRecorder.start()

    // Listen for a time
    while (!stop.value){
        kotlinx.coroutines.delay(1)
    }
    textContent.value = "Processing ..."
    stop.value = false  // Reset after finished

    mediaRecorder.stop()
    mediaRecorder.release()

    val file = File(audioFileName)
    val fileInputStream = FileInputStream(file)
    val requestBody = file.asRequestBody("audio/*".toMediaTypeOrNull())

    val multipartBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("file", file.name, requestBody)
        .addFormDataPart("model", "whisper-1")
        .build()

    val request = Request.Builder()
        .url("https://api.openai.com/v1/audio/transcriptions")
        .header("Authorization", "Bearer $OPENAI_KEY")
        .post(multipartBody)
        .build()

    val client = OkHttpClient()

    var response: Response? = null
    var attempts = 0

    while (response == null && attempts < 3) {
        try {
            response = withTimeout(10_000L) {
                withContext(Dispatchers.IO) { client.newCall(request).execute() }
            }
        } catch (e: Throwable) {
            attempts++
            if (attempts >= 3) {
                throw e
            }
        }
    }

    val result = response?.body?.string()

    // Delete the audio file
    file.delete()

    // Parse the JSON response to extract the transcribed text
    val jsonObject = JSONObject(result)
    return jsonObject.getString("text")
}

// Text To Speech Functions =======================
suspend fun identifyLanguage(text: String, onComplete: (String) -> Unit) {
    try {
        val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { result ->
                if (result != LanguageIdentifier.UNDETERMINED_LANGUAGE_TAG) {
                    onComplete(result)
                } else {
                    onComplete("en")
                }
            }
            .addOnFailureListener { e ->
                onComplete("$e")
            }

    } catch (e: Throwable) {
        "$e"
    }
}

suspend fun use_openai_tts(
    context: Context,
    inputText: String,
    speaking: MutableState<Boolean>,
    assistant: AssistantSettings,
    background_color: MutableState<Color>,
    text_color: MutableState<Color>
) {
    speaking.value = true
    set_active_colors(assistant, background_color, text_color)

    val client = HttpClient(Android) {
        // Configure the client if needed
    }

    val requestBody = buildJsonObject {
        put("model", "tts-1")
        put("input", inputText)
        put("voice", openai_voice)
    }

    // Use the appropriate serializer for the JSON body (e.g., kotlinx.serialization.json.Json)
    val requestBodyString = requestBody.toString()

    try {
        // Step 1: Call the audio generation API
        val response: HttpResponse = client.post("https://api.openai.com/v1/audio/speech") {
            header("Authorization", "Bearer $OPENAI_KEY")
            header("Content-Type", "application/json")
            body = requestBodyString
        }

        // Check if the response is successful
        if (response.status == HttpStatusCode.OK) {
            // Step 2: Save the received audio data to a file
            withContext(Dispatchers.IO) {
                val audioFile = File(context.cacheDir, "generated_audio.mp3")
                audioFile.writeBytes(response.readBytes())

                // Step 3: Play the audio file and wait for completion
                suspendCancellableCoroutine<Unit> { cont ->
                    val mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioFile.absolutePath)
                        setOnCompletionListener {
                            // Step 4: Delete the file after playback and resume the coroutine
                            audioFile.delete()
                            it.release()
                            cont.resume(Unit)
                        }
                        prepare()
                        start()
                    }

                    cont.invokeOnCancellation {
                        mediaPlayer.release()
                    }
                }
            }
        } else {
            // Handle the error case
        }
    } catch (e: Exception) {
        // Handle exceptions
        e.printStackTrace()
    } finally {
        client.close()
        speaking.value = false
        reset_colors(background_color, text_color)
    }
}

suspend fun use_native_tts(context: Context, text: String,
                           lang: String = "en",
                           speaking: MutableState<Boolean>,
                           assistant: AssistantSettings,
                           background_color: MutableState<Color>,
                           text_color: MutableState<Color>
                           ) = suspendCancellableCoroutine<Unit> { continuation ->
     textToSpeech = TextToSpeech(context) { status ->
         speaking.value = true
         set_active_colors(assistant, background_color, text_color)
        if (status != TextToSpeech.ERROR) {
            textToSpeech.language = Locale.forLanguageTag(lang)

            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Handle TTS onStart event (optional)
                }

                override fun onDone(utteranceId: String?) {
                    // Handle TTS onDone event (optional)
                    continuation.resume(Unit)
                }

                override fun onError(utteranceId: String?) {
                    // Handle TTS onError event (optional)
                    continuation.resume(Unit)
                }
            })

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
        } else {
            continuation.resume(Unit)
        }
         speaking.value = false
         reset_colors(background_color, text_color)
    }
}
suspend fun use_device_pronounced_tts(text: String, coroutineScope: CoroutineScope,
                                      context: Context,
                                      speaking: MutableState<Boolean>,
                                      assistant: AssistantSettings,
                                      background_color: MutableState<Color>,
                                      text_color: MutableState<Color>){
    identifyLanguage(text) { languageCode ->
        coroutineScope.launch(Dispatchers.Main) {
            use_native_tts(context, "$text",
                lang = languageCode,
                speaking,
                assistant,
                background_color,
                text_color)
        }
    }
}

suspend fun openai_tts_is_method(context: Context): Boolean{
    /*
    Determines if user changed setting to use OpenAI TTS.

    If true, OpenAI; else, on-device
     */

    if (file_exists(context, "tts.txt")){
        return read_text_from_file(context, "tts.txt").contains("true", ignoreCase = true)
    }

    return false
}

suspend fun toggle_tts(context: Context): Boolean{
    var use_openai = true

    if (file_exists(context, "tts.txt")){
        use_openai = read_text_from_file(context, "tts.txt").contains("true", ignoreCase = true)

        if (use_openai){
            write_text_to_file(context, "tts.txt", "false")
            return false
        } else {
            write_text_to_file(context, "tts.txt", "true")
            return true
        }
    }

    return true  // if no file, default is use
}

suspend fun setup_tts_file(context: Context){
    if (!file_exists(context, "tts.txt")){
        write_text_to_file(context, "tts.txt", "true")
    }
}