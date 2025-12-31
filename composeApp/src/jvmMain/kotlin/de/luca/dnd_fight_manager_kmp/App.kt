package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class)
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
                    .background(Color.Gray),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { fighters.add(Fighter(mutableStateOf("Fighter-${count++}"))) },
                    content = { Text("+") },
                    modifier = Modifier.padding(5.dp)
                )
                Text("Kämpfer-Liste")
                Button(
                    onClick = { fighters.sortByDescending { it.initiative.value } },
                    content = { Text("Sortieren") },
                    modifier = Modifier.padding(5.dp)
                )
            }
            Box{
                Box(Modifier.background(Color.Gray).fillMaxSize())
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        Modifier
                            .height(15.dp)
                            .fillMaxWidth()
                            .background(Color.White)
                    )
                }
                Box {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(15.dp))
                        .padding(0.dp, 10.dp, 0.dp, 10.dp),
                        state = listState) {
                        itemsIndexed(
                            items = fighters,
                            key = { _, fighter: Fighter -> fighter.id }
                        ) {
                                index: Int, fighter: Fighter ->
                            Box(Modifier.animateItem()) {
                                fighters.get(index).paintListElement(removeFighter, index)
                            }
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = listState),
                        style = ScrollbarStyle(
                            minimalHeight = 16.dp,
                            thickness = 8.dp,
                            shape = RoundedCornerShape(4.dp),
                            hoverDurationMillis = 300,
                            unhoverColor = Color.Gray,
                            hoverColor = Color.DarkGray
                        )
                    )}
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