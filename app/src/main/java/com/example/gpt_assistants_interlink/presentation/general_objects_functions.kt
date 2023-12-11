package com.example.gpt_assistants_interlink.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.gson.Gson

class StateTracker(){
    val waiting = false
    val working = false
}

data class ID_Name_Prompt(
    var id: String,
    var name: String,
    var prompt: String)

fun reset_colors(background: MutableState<Color>, text: MutableState<Color>){
    background.value = Color.Black
    text.value = Color.White
}

fun set_active_colors(assistantSettings: AssistantSettings,
                      background: MutableState<Color>,
                      text: MutableState<Color>){
    background.value = assistantSettings.screen_color
    text.value = assistantSettings.text_color
}

fun vibrateWatch(context: Context, miliseconds: Long = 500L, effect: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Check if the device has a vibrator.
    if (vibrator.hasVibrator()) {
        // New VibrationEffect API available from API level 26 (Android 8.0 Oreo).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect =
                VibrationEffect.createOneShot(500, effect)
            vibrator.vibrate(vibrationEffect)
        } else {
            // Deprecated in API 26, use the VibrationEffect api above instead.
            @Suppress("DEPRECATION")
            vibrator.vibrate(miliseconds)
        }
    }
}

suspend fun get_color(prompt: String): Color {
    val gpt = Chatbot("gpt-3.5-turbo-16k", COLOR_SETTING_PROMPT)
    var selected_color: GPTColor
    val gson = Gson()

    var ct = 0
    var success = false
    while (ct < 3 && !success){
        try{
            selected_color = gson.fromJson(gpt.say_to_chatbot(prompt, 500), GPTColor::class.java)
            success = true
            return Color(selected_color.rgb[0], selected_color.rgb[1], selected_color.rgb[2])
        } catch (e: Exception){
            ct++
        }
    }

    throw Exception("Color unable to be parsed after 3 attempts")
    return Color.Gray
}

fun get_readable_color(color: Color): Color {
    // Calculate the luminance of the input color.
    val luminance = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)

    // If the luminance is above the threshold, return black; otherwise, return white.
    val threshold = 0.5 // This may vary; common values are between 0.5 and 0.7
    return if (luminance > threshold) Color.Black else Color.White
}
