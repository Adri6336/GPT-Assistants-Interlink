/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.gpt_assistants_interlink.presentation

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
            AppContent()
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
    var selected = false

    var screen_locked = remember { mutableStateOf(true) }
    var setting_up = remember { mutableStateOf(true) }
    var talking_to_api = remember { mutableStateOf(false) }
    var setup_presses = remember { mutableStateOf(0) }

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

                try{
                    load_ids(context)
                } catch (e: Exception){
                    instantiate_or_connect_swarm(context)
                }

                if (setup_presses.value > 3){

                }


                buttonText.value = "Ready For Use"
                buttonColor.value = Color.Black
                screen_locked.value = false
                ready.value = true


                setting_up.value = false

            } catch (e: Exception) {
                // Handle the exception properly
                Log.d("Error", "Error during pre-instantiating thread: ${e.toString()}")
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
                // TEST
                var response = ""

                if (ready.value && !screen_locked.value){
                    // Add code here
                    coroutineScope.launch(Dispatchers.Main) {
                        try{
                            screen_locked.value = true  // Prevent user from tapping again

                            val prompt = "Heyo! :D"

                            if (selected){
                                buttonText.value = "${assistant.value.name} thinking ..."
                                buttonColor.value = assistant.value.screen_color
                                buttonTextColor.value = assistant.value.text_color
                                response = gpt.value.say_to_assistant(prompt)

                            } else {
                                assistant.value = select_assistant(prompt)
                                selected = true
                                gpt.value = GPT(assistant.value.assistant_id)
                                gpt.value.load_or_create_thread(context)

                                buttonText.value = "${assistant.value.name} thinking ..."
                                buttonColor.value = assistant.value.screen_color
                                buttonTextColor.value = assistant.value.text_color
                                response = gpt.value.say_to_assistant(prompt)
                            }


                            buttonColor.value = Color.Black
                            buttonTextColor.value = Color.White
                            buttonText.value = response
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
                }
            },
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