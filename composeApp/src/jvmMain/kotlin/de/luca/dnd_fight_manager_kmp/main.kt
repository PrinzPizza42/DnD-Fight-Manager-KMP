package de.luca.dnd_fight_manager_kmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DnD-Fight-Manager-KMP",
    ) {
        App()
    }
}