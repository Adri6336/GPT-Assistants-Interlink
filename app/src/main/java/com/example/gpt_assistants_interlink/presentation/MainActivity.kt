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
    var ready = remember { mutableStateOf(false) }
    var gpt = remember {GPT("asst_qroDjVhky67l3wfAq3LnqAxw")}
    var main_thread: ThreadObject
    var step = "start"

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){}

    LaunchedEffect(Unit){
        coroutineScope.launch {
            try {
                step = "thread start"
                buttonColor.value = Color.Blue
                buttonText.value = "Setting Up Interlink"
                main_thread = gpt.create_thread()
                buttonText.value = "Ready For Use"
                buttonColor.value = Color.Black
                ready.value = true

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
                if (ready.value){
                    // Add code here


                } else {
                    coroutineScope.launch(Dispatchers.Main) {

                        try{
                            buttonColor.value = Color.Red
                            buttonText.value = "C=59(F−32)The equation above shows how temperature F, measured in degrees Fahrenheit, relates to a temperature C, measured in degrees Celsius. Based on the equation, which of the following must be true?A temperature increase of 1 degree Fahrenheit is equivalent to a temperature increase of 59 degree Celsius.A temperature increase of 1 degree Celsius is equivalent to a temperature increase of 1.8 degrees Fahrenheit.A temperature increase of 59 degree Fahrenheit is equivalent to a temperature increase of 1 degree Celsius."

                            buttonColor.value = Color.Magenta
                            buttonText.value = "Thinking ..."

                            step = "create message"
                            val create_message_response = gpt.create_message("C=59(F−32)The equation above shows how temperature F, measured in degrees Fahrenheit, relates to a temperature C, measured in degrees Celsius. Based on the equation, which of the following must be true?A temperature increase of 1 degree Fahrenheit is equivalent to a temperature increase of 59 degree Celsius.A temperature increase of 1 degree Celsius is equivalent to a temperature increase of 1.8 degrees Fahrenheit.A temperature increase of 59 degree Fahrenheit is equivalent to a temperature increase of 1 degree Celsius.")

                            step = "run thread"
                            val run = gpt.run_thread()

                            step = "get state and check into status"
                            var run_state = run.status
                            var ct = 0
                            while (run_state != "completed" && run_state != "expired" && run_state != "failed"){
                                delay(1000)
                                run_state = gpt.run_status(run)
                                buttonText.value = "Thinking ...\nPolled: $ct times"
                                ct++
                            }

                            step = "get new message"
                            val response = gpt.get_newest_message()

                            buttonColor.value = Color.Black
                            buttonText.value = response
                            ready.value = true
                        } catch (e: Exception){
                            buttonColor.value = Color.Black
                            buttonText.value = "$step\n${e.toString()}"
                            Log.d("Error", "$step\n${e.toString()}")
                            ready.value = true
                        }


                    }
                }




            },
            // Use a custom color scheme for the button
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor.value,
                contentColor = Color.White
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

fun changeButtonState(
    text: String,
    color: Color,
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit
) {
    val newText = if (text == "Press Me") "Pressed" else "Press Me"
    val newColor = if (color == Color.Black) Color.Blue else Color.Gray

    onTextChange(newText)
    onColorChange(newColor)
}