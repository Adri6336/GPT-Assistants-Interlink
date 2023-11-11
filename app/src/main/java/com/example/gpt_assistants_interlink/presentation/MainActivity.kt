/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.gpt_assistants_interlink.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var buttonText = remember { mutableStateOf("Press Me") }
    var buttonColor = remember { mutableStateOf(Color.Black) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                changeButtonState(
                    buttonText.value,
                    buttonColor.value,
                    onTextChange = { newText -> buttonText.value = newText },
                    onColorChange = { newColor -> buttonColor.value = newColor }
                )
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