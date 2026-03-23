package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dnd_fight_manager_kmp.composeapp.generated.resources.Res
import dnd_fight_manager_kmp.composeapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val title = remember { mutableStateOf("DnD-Fight-Manager-KMP") }
    val icon = painterResource(Res.drawable.icon)

    Window(
        onCloseRequest = ::exitApplication,
        title = title.value,
        icon = icon
    ) {
        App(title)
    }
}