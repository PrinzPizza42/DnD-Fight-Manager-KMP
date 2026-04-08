package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.window.application
import de.luca.dnd_fight_manager_kmp.WindowManager.mainWindowTitle
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun main() = application {
    remember {
        WindowManager.openNewWindow(
            content = { App(mainWindowTitle) },
            onCloseRequest = { exitApplication() }
        )
        true
    }

    for (window in WindowManager.windowList) {
        key(window.uuid) {
            window.draw()
        }
    }
}