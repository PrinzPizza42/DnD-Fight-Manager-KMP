package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    val title = remember { mutableStateOf("DnD-Fight-Manager-KMP") }

    Window(
        onCloseRequest = ::exitApplication,
        title = title.value,
    ) {
        App(title)
    }
}