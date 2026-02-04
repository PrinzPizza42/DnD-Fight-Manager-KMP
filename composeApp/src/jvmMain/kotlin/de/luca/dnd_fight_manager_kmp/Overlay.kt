package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

object Overlay {
    val activeOverlay = mutableStateOf<(@Composable () -> Unit)?>(null)
    val isActive: Boolean = activeOverlay.value != null

    fun showOverlay(content: @Composable () -> Unit) {
        activeOverlay.value = content
    }

    fun closeOverlay() { activeOverlay.value = null }

    fun showAddGroupOverlay() {
        showOverlay({
            Box(
                Modifier
                    .size(500.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(20.dp)
            ) {
                Column {
                    val name = remember { mutableStateOf("") }

                    textField(name, "Name")

                    val color = remember { mutableStateOf(Color.random()) }

                    Box(
                        Modifier
                            .size(20.dp)
                            .background(color.value, RoundedCornerShape(5.dp))
                    )
                    // TODO: Add color manipulator with fitting inputs

                    Row {
                        Button(
                            onClick = {
                                GroupManager.addGroup(Group(name = name, color = color))
                                closeOverlay()
                            },
                            content = { Text("Hinzufügen") },
                            modifier = Modifier.padding(5.dp)
                        )
                        Button(
                            onClick = { closeOverlay() },
                            content = { Text("Abbrechen") },
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        })
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun showAllGroupsOverlay() {
        showOverlay({
            MaterialTheme {
                Box(
                    Modifier
                        .size(500.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Alle Gruppen:")
                        if(GroupManager.groups.isEmpty()) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Keine Gruppen vorhanden")
                            }
                        }
                        else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 10.dp)
                                    .background(Color(0xFFEEEEEE), RoundedCornerShape(5.dp))
                            ) {
                                items(GroupManager.groups) { group ->
                                    var isHovered by remember { mutableStateOf(false) }
                                    val shadow by animateDpAsState(if(isHovered) 5.dp else 0.dp, tween(200))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(70.dp)
                                            .padding(5.dp)
                                            .shadow(shadow, shape = RoundedCornerShape(10.dp))
                                            .background(Color.LightGray, RoundedCornerShape(10.dp))
                                            .padding(5.dp)
                                            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                            .onPointerEvent(PointerEventType.Exit) { isHovered = false },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            Modifier
                                                .size(20.dp)
                                                .background(group.color.value, RoundedCornerShape(5.dp))
                                        )
                                        Text(group.name.value, modifier = Modifier.weight(1f))
                                        Text("(${group.fighters.size})")

                                        var showDeletePopup by remember { mutableStateOf(false) }
                                        Button(
                                            onClick = {
                                                showDeletePopup = true
                                            },
                                            content = { Text("Löschen") },
                                            modifier = Modifier.padding(5.dp)
                                        )

                                        if(showDeletePopup) {
                                            Popup(
                                                onDismissRequest = { showDeletePopup = false }
                                            ) {
                                                Column(
                                                    Modifier
                                                        .shadow(10.dp)
                                                        .background(Color.White, RoundedCornerShape(10.dp))
                                                        .padding(5.dp)
                                                ) {
                                                    Text("Auch alle Mitglieder der Gruppe entfernen?")
                                                    Text("(Wenn nicht sind sie einfach keiner Gruppe mehr zugeordnet)")
                                                    Row {
                                                        Button(
                                                            onClick = {
                                                                GroupManager.removeGroupWithAllFighters(group)
                                                                showDeletePopup = false
                                                            },
                                                            content = { Text("Ja") },
                                                            modifier = Modifier.padding(5.dp)
                                                        )
                                                        Button(
                                                            onClick = {
                                                                GroupManager.removeGroup(group)
                                                                showDeletePopup = false
                                                            },
                                                            content = { Text("Nein") },
                                                            modifier = Modifier.padding(5.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { closeOverlay() },
                            content = { Text("Zurück") },
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        })
    }
}