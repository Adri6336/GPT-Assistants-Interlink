/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.gpt_assistants_interlink.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.gpt_assistants_interlink.presentation.theme.GPTAssistantsInterlinkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            GPTAssistantsInterlinkTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    val context = LocalContext.current

    RequestPermissions(
        onAllPermissionsGranted = {
            // All Permissions are granted,
            // Proceed with displaying your main content or navigation.
        },
        onNotAllPermissionsGranted = {
            // Not all Permissions are granted,
            // Show a message to the user or take appropriate action.
            Toast.makeText(
                context,
                "Not all permissions granted. The app may not work as intended.",
                Toast.LENGTH_LONG
            ).show()
        }
    )

    AppContent() // Your main app composable goes here, it will display the rest of the UI.
}

@Composable
fun RequestPermissions(
    onAllPermissionsGranted: () -> Unit,
    onNotAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val permissionsToRequest = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.VIBRATE
    )

    // Always remember the launcher
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all permissions are granted
        if (permissions.all { it.value }) {
            onAllPermissionsGranted()
        } else {
            onNotAllPermissionsGranted()
        }
    }

    // In Compose, effects such as LaunchedEffect are used to handle lifecycle events like launching the permissions request.
    // permissionsToRequest.any() should be used as the key to re-launch the permissions request when needed.
    LaunchedEffect(key1 = permissionsToRequest.any {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }) {
        // Only launch the permission request if needed
        if (permissionsToRequest.any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED }) {
            multiplePermissionsLauncher.launch(
                permissionsToRequest.toTypedArray()
            )
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    var buttonText = remember { mutableStateOf("Press to Load") }
    var buttonColor = remember { mutableStateOf(Color.Black) }
    var buttonTextColor = remember { mutableStateOf(Color.White) }

    var ready = remember { mutableStateOf(false) }
    var gpt = remember { mutableStateOf(GPT("asst_qroDjVhky67l3wfAq3LnqAxw"))}
    var main_thread: ThreadObject
    var step = "start"
    var assistant = remember { mutableStateOf(assistants[1]) }
    var selected = remember {
        mutableStateOf(false)
    }

    var screen_locked = remember { mutableStateOf(true) }
    var setting_up = remember { mutableStateOf(true) }
    var talking_to_api = remember { mutableStateOf(false) }
    var setup_presses = remember { mutableStateOf(0) }
    var speaking = remember { mutableStateOf(false) }
    var openai_tts = remember {
        mutableStateOf(true)
    }
    var stop_listening = remember { mutableStateOf(false) }
    var last_prompt = remember { mutableStateOf("") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){}

    LaunchedEffect(Unit){
        coroutineScope.launch {
            try {

                step = "thread start"
                buttonColor.value = Color.Blue
                buttonText.value = "Setting Up Interlink"
                delay(1500)

                setup_tts_file(context)
                openai_tts.value = openai_tts_is_method(context)

                try{
                    load_ids(context)
                } catch (e: Exception){
                    instantiate_or_connect_swarm(context)
                }

                if (setup_presses.value > 3){
                    openai_tts.value = toggle_tts(context)
                }


                buttonText.value = "Ready For Use"
                buttonColor.value = Color.Black
                screen_locked.value = false
                ready.value = true


                setting_up.value = false

            } catch (e: Exception) {
                // Handle the exception properly
                Log.d("Error", "Error during pre-instantiating thread: ${e.toString()}")
                vibrateWatch(context, 200L, VibrationEffect.EFFECT_HEAVY_CLICK)
                buttonColor.value = Color.Gray
                buttonText.value = "Error during pre-instantiating thread: ${e.toString()}"
            }

        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                )

                {

                var response = ""
                var switch = false
                var system_command = false

                if (ready.value && !screen_locked.value && !speaking.value){
                    // START ================
                    coroutineScope.launch {
                        try{
                            var flagged = false
                            screen_locked.value = true  // Prevent user from tapping again

                            // RECORD AND MODERATE ==============
                            buttonColor.value = Color.Blue
                            var prompt = recordAudioAndTranscribe(context, stop_listening,
                            buttonText).toString()  // This is voodoo. How does it work? Magic
                            /*
                            Legit this is a copy-paste function I made a long time ago. I've spent some time trying
                            to understand how it works in relation to everything else (tap to record and tap to stop)
                            but I've come up empty. It may be that I'm tired af tho lol
                             */


                            // PROCESSING ==============
                            if (prompt.contains("please connect me", ignoreCase = true) ||
                                prompt.contains("please connect to", ignoreCase = true) ||
                                prompt.contains("please connect your", ignoreCase = true)){
                                system_command = true
                                last_prompt.value = prompt
                                var connect_message = ""

                                if (prompt.contains("translat", ignoreCase = true)){
                                    assistant.value = assistants[0]

                                } else if (prompt.contains("generalist", ignoreCase = true)){
                                    assistant.value = assistants[1]

                                } else if (prompt.contains("engineer", ignoreCase = true)){
                                    assistant.value = assistants[2]

                                } else if (prompt.contains("friend", ignoreCase = true)){
                                    assistant.value = assistants[3]

                                } else if (prompt.contains("advisor", ignoreCase = true)){
                                    assistant.value = assistants[4]

                                } else if (prompt.contains("math", ignoreCase = true)){
                                    assistant.value = assistants[5]

                                } else if (prompt.contains("scientist", ignoreCase = true)){
                                    assistant.value = assistants[6]

                                } else if (prompt.contains("psych", ignoreCase = true) ||
                                           prompt.contains("coach", ignoreCase = true)){
                                    assistant.value = assistants[7]

                                } else {
                                    assistant.value = select_assistant(prompt)  // Use language model if uncertain
                                }


                                selected.value = true

                                gpt.value = GPT(assistant.value.assistant_id)
                                gpt.value.load_or_create_thread(context)
                                connect_message = "Connected to ${assistant.value.name}"
                                vibrateWatch(context)
                                buttonText.value = connect_message
                                use_device_pronounced_tts(connect_message, coroutineScope,
                                    context, speaking,
                                    assistant.value, buttonColor,
                                    buttonTextColor)


                            } else if (prompt.contains("please reboot system")){
                                last_prompt.value = prompt
                                system_command = true
                                screen_locked.value = true
                                buttonText.value = "Purging Assistants ..."

                                use_device_pronounced_tts("Purging assistants ...", coroutineScope,
                                    context, speaking,
                                    assistant.value, buttonColor,
                                    buttonTextColor)

                                purge_assistants(context)
                                instantiate_or_connect_swarm(context)
                                screen_locked.value = false
                                buttonText.value = "Completed purge"

                            } else if (prompt.contains("please display last message", ignoreCase = true)){
                                system_command = true
                                buttonText.value = last_prompt.value
                                buttonColor.value = Color.Black

                            } else if (prompt.contains("please clear memory", ignoreCase = true) ||
                                    prompt.contains("please wipe memory", ignoreCase = true)){
                                system_command = true
                                delete_file(context, "${assistant.value.assistant_id}.txt")
                                gpt.value.load_or_create_thread(context)

                                buttonColor.value = Color.Black
                                buttonText.value = "Memory wiped for ${assistant.value.name}."
                                vibrateWatch(context)

                            } else if (prompt.contains("please wipe all memory", ignoreCase = true) ||
                                    prompt.contains("Please clear all memory", ignoreCase = true)){
                                system_command = true
                                for (setting in assistants){
                                    if (file_exists(context, "${setting.assistant_id}.txt")){
                                        delete_file(context, "${setting.assistant_id}.txt")
                                    }
                                }

                                gpt.value.load_or_create_thread(context)

                                buttonColor.value = Color.Black
                                buttonText.value = "All memory wiped"
                                vibrateWatch(context)
                            }

                            // Enter command processing code here
                            flagged = moderate(prompt)

                            if (!system_command){  // Only process non-commands
                                last_prompt.value = prompt
                                if (selected.value){  // Thread already loaded
                                    buttonText.value = "${assistant.value.name} thinking ..."
                                    buttonColor.value = Color.Red
                                    buttonTextColor.value = Color.White
                                    if (!flagged){
                                        response = gpt.value.say_to_assistant(prompt)
                                    }


                                } else {  // Load a new thread
                                    assistant.value = select_assistant(prompt)
                                    selected.value = true
                                    gpt.value = GPT(assistant.value.assistant_id)
                                    gpt.value.load_or_create_thread(context)

                                    buttonText.value = "${assistant.value.name} thinking ..."
                                    buttonColor.value = Color.Red
                                    buttonTextColor.value = Color.White
                                    if (!switch && !flagged){
                                        response = gpt.value.say_to_assistant(prompt)
                                    }
                                }

                                if (!switch && !flagged){  
                                    val summary_bot = Chatbot(GPT_MODEL, SUMMARY_SYS_PROMPT)
                                    val summary = summary_bot.say_to_chatbot(response, 4095)

                                    if (assistant.value.name != "GAI-translator"){
                                        buttonText.value = summary
                                    } else {
                                        buttonText.value = response
                                    }
                                } else {
                                    buttonText.value = "Connected to ${assistant.value.name}."
                                    response = "Connected to ${assistant.value.name}."
                                    switch = false
                                }


                                // Process TTS ============
                                if (!openai_tts.value || flagged){
                                    use_device_pronounced_tts(response, coroutineScope,
                                        context, speaking,
                                        assistant.value, buttonColor,
                                        buttonTextColor)
                                } else {
                                    use_openai_tts(context, response,
                                        speaking,
                                        assistant.value,
                                        buttonColor,
                                        buttonTextColor)
                                }
                            }

                            system_command = false
                            screen_locked.value = false

                        } catch (e: Exception){
                            Log.d("Error", e.toString())
                            buttonColor.value = Color.Gray
                            buttonText.value = e.toString()
                            screen_locked.value = false
                        }
                    }

                } else { // Screen is locked
                    if (setting_up.value && !talking_to_api.value){
                        setup_presses.value++
                    }

                    stop_listening.value = true
                }
            }},
            // Use a custom color scheme for the button
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor.value,
                contentColor = buttonTextColor.value
            ),
            modifier = Modifier.size(300.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = buttonText.value,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }
}