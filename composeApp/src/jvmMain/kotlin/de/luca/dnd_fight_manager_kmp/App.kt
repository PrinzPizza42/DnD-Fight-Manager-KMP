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
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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

        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                Button(
                    onClick = { fighters.add(Fighter()) },
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
fun textField(onChange: (String) -> Unit, input: String, label: String) {
    val focusManager = LocalFocusManager.current

    var value: String by remember { mutableStateOf(input) }

    MaterialTheme {
        OutlinedTextField(
            value = value,
            onValueChange = { text ->
                onChange(text)
                value = text
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
fun textFieldInt(onChange: (Int) -> Unit, input: Int, label: String) {
    val focusManager = LocalFocusManager.current

    var value: String by remember { mutableStateOf(input.toString()) }

    MaterialTheme {
        OutlinedTextField(
            value = value,
            onValueChange = { text: String ->
                value = text
                if(value.toIntOrNull() != null) onChange(text.toInt())
            },
            modifier = Modifier.onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    if(value.toIntOrNull() == null) return@onPreviewKeyEvent false

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
            isError = value.toIntOrNull() == null,
            label = { label },
            singleLine = true
        )
    }
}