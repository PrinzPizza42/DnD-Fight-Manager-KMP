package de.luca.dnd_fight_manager_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val fighters = remember { mutableStateListOf<Fighter>() }

        val removeFighter = {
            fighter: Fighter ->
            fighters.remove(fighter)
        }

        var count = 0

        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                Button(
                    onClick = { fighters.add(Fighter(mutableStateOf("Fighter-${count++}"))) },
                    content = { Text("+") },
                    modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp)
                )
                Text("Kämpfer-Liste")
            }
            LazyColumn {
                items(fighters.size) { index ->
                    fighters.get(index).paintListElement(removeFighter, index)
                }
            }
        }

    }
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