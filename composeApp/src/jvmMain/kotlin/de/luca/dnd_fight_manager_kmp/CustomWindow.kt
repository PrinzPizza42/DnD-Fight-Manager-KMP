package de.luca.dnd_fight_manager_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CustomWindow @OptIn(ExperimentalUuidApi::class) constructor(
    val uuid: Uuid = Uuid.random(),
    val title: MutableState<String>,
    val onCloseRequest: () -> Unit,
    val icon: DrawableResource,
    private var open: MutableState<Boolean> = mutableStateOf(true)
) {
    @OptIn(ExperimentalUuidApi::class)
    var content: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize().background(Color.White, RoundedCornerShape(10.dp))) {
            Text("Window ID: $uuid")
        }
    }

    @Composable
    fun draw() {
        Window(
            onCloseRequest = {
                onCloseRequest()
                hide()
            },
            title = title.value,
            icon = painterResource(icon),
            visible = open.value
        ) {
            content()
        }
    }

    fun hide() {
        open.value = false
        WindowManager.removeWindow(this)
    }

    fun show() {
        open.value = true
    }
}