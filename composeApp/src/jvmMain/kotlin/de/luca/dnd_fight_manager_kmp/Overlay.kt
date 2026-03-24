package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.uuid.ExperimentalUuidApi

object Overlay {
    val activeOverlay = mutableStateOf<(@Composable () -> Unit)?>(null)
    val isActive: Boolean = activeOverlay.value != null

    fun showOverlay(content: @Composable () -> Unit) {
        activeOverlay.value = content
    }

    fun closeOverlay() { activeOverlay.value = null }

    @OptIn(ExperimentalUuidApi::class)
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

                    textField(name, "Name", Modifier.fillMaxWidth())

                    val color = remember { mutableStateOf(Color.random()) }
                    val showPopup = remember { mutableStateOf(false) }

                    colorElement(color, showPopup)
                    Box(Modifier.weight(1f))
                    Row {
                        Button(
                            onClick = {
                                GroupManager.add(Group(name = name, color = color))
                                closeOverlay()
                            },
                            content = { Text("Hinzufügen") },
                            modifier = Modifier.padding(5.dp)
                        )
                        Box(Modifier.weight(1f))
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Alle Gruppen")
                            Box(Modifier.weight(1f))
                            IconButton(
                                onClick = { closeOverlay() },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Schließen"
                                    )
                                }
                            )
                        }
                        if(GroupManager.groups.isEmpty()) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Keine Gruppen gefunden", color = Color.Gray)
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
                                                .padding(horizontal = 10.dp)
                                                .size(20.dp)
                                                .background(group.color.value, RoundedCornerShape(5.dp))
                                        )
                                        Text(group.name.value, modifier = Modifier.weight(1f))
                                        Text("(${group.fighters.value.size})")

                                        val showEditGroupPopup = remember { mutableStateOf(false) }
                                        IconButton(
                                            onClick = { showEditGroupPopup.value = true },
                                            content = { Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editieren"
                                            ) },
                                            modifier = Modifier.padding(5.dp)
                                        )
                                        if(showEditGroupPopup.value) editGroupPopup(showEditGroupPopup, group)

                                        val showDeletePopup = remember { mutableStateOf(false) }
                                        IconButton(
                                            onClick = { showDeletePopup.value = true },
                                            content = { Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Löschen"
                                            ) },
                                            modifier = Modifier.padding(5.dp)
                                        )

                                        if(showDeletePopup.value) deleteGroupPopup(showDeletePopup, group)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    @Composable
    fun editGroupPopup(showEditGroupPopup: MutableState<Boolean>, group: Group) {
        Popup(
            onDismissRequest = { showEditGroupPopup.value = false },
            alignment = Alignment.Center,
            properties = PopupProperties(focusable = true)
        ) {
            Column(
                Modifier
                    .shadow(5.dp, shape = RoundedCornerShape(10.dp))
                    .width(350.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(group.name.value, modifier = Modifier.padding(5.dp))
                    Box(Modifier.weight(1f))
                    IconButton(
                        onClick = { showEditGroupPopup.value = false },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Schließen"
                            )
                        },
                        modifier = Modifier.padding(5.dp)
                    )
                }

                val name = remember { mutableStateOf(group.name.value) }
                Row {
                    textField(name, "Name", Modifier.width(280.dp))
                    IconButton(
                        onClick = { group.name.value = name.value },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Übernehmen"
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }

                colorElement(group.color)
            }
        }
    }

    @Composable
    fun deleteGroupPopup(showDeletePopup: MutableState<Boolean>, group: Group) {
        Popup(
            onDismissRequest = { showDeletePopup.value = false }
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
                            GroupManager.deleteGroupWithAllFighters(group)
                            showDeletePopup.value = false
                        },
                        content = { Text("Ja") },
                        modifier = Modifier.padding(5.dp)
                    )
                    Button(
                        onClick = {
                            GroupManager.deleteGroupWithoutFighters(group)
                            showDeletePopup.value = false
                        },
                        content = { Text("Nein") },
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }
}