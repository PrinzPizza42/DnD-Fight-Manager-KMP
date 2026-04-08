package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dnd_fight_manager_kmp.composeapp.generated.resources.Res
import dnd_fight_manager_kmp.composeapp.generated.resources.icon
import org.jetbrains.compose.resources.DrawableResource
import kotlin.uuid.ExperimentalUuidApi

object WindowManager {
    val mainWindowTitle = mutableStateOf("DnD-Fight-Manager-KMP")
    val iconRessource = Res.drawable.icon
    val windowList = mutableStateListOf<CustomWindow>()

    @OptIn(ExperimentalUuidApi::class)
    fun openNewWindow(
        onCloseRequest: () -> Unit,
        content: @Composable (() -> Unit)? = null,
        icon: DrawableResource = iconRessource,
        title: MutableState<String> = mainWindowTitle
    ): CustomWindow {
        val newWindow = CustomWindow(
            onCloseRequest = onCloseRequest,
            icon = icon,
            title = title,
        )
        if(content != null) newWindow.content = content

        windowList.add(newWindow)

        return newWindow
    }

    fun removeWindow(window: CustomWindow) {
        windowList.remove(window)
    }
}