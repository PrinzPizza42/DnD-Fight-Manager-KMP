package de.luca.dnd_fight_manager_kmp

import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalFocusManager
import kotlin.random.Random

fun Color.Companion.random(): Color {
    return Color.hsv(
        hue = Random.nextFloat() * 360f,
        saturation = 1f,
        value = 1f
    )
}

@Composable
fun textField(input: MutableState<String>, label: String) {
    val focusManager = LocalFocusManager.current

    MaterialTheme {
        OutlinedTextField(
            value = input.value,
            onValueChange = { text ->
                input.value = text
            },
            modifier = Modifier.onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Enter -> {
                            focusManager.clearFocus()
                            true
                        }

                        Key.Escape -> {
                            focusManager.clearFocus()
                            true
                        }

                        Key.Backspace -> false
                        else -> true
                    }
                } else {
                    false
                }
            },
            label = { label },
            singleLine = true
        )
    }
}

@Composable
fun textFieldInt(input: MutableState<Int>, label: String) {
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }

    MaterialTheme {
        OutlinedTextField(
            value = input.value.toString(),
            onValueChange = { text: String ->
                if(text.toIntOrNull() != null) input.value = text.toInt()
            },
            modifier = Modifier.onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    val isDigit = event.utf16CodePoint.toChar().isDigit()

                    // 2. Ist es eine erlaubte Navigationstaste?
                    // (Wichtig, damit der User korrigieren oder den Cursor bewegen kann)
                    val isNavKey = event.key in listOf(
                        Key.Backspace, Key.Delete,
                        Key.DirectionLeft, Key.DirectionRight,
                        Key.DirectionUp, Key.DirectionDown,
                        Key.Tab, Key.Enter, Key.MoveHome, Key.MoveEnd, Key.Escape
                    )

                    if(!isDigit && !isNavKey) {
                        isError = true
                        return@onPreviewKeyEvent true
                    }
                    else {
                        isError = false
                        when (event.key) {
                            Key.Enter -> {
                                focusManager.clearFocus()
                                true
                            }

                            Key.Escape -> {
                                focusManager.clearFocus()
                                true
                            }

                            Key.Backspace -> {
                                val canBackspace = input.value.toString().length <= 1
                                if(canBackspace) input.value = 0
                                return@onPreviewKeyEvent canBackspace
                            }
                            else -> true
                        }
                        return@onPreviewKeyEvent false
                    }

                } else {
                    false
                }
            },
            isError = isError,
            label = { label },
            singleLine = true
        )
    }
}