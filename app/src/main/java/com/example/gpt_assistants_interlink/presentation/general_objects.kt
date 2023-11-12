package com.example.gpt_assistants_interlink.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

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