package com.example.gpt_assistants_interlink.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class StateTracker(){
    val waiting = false
    val working = false
}

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
