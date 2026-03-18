package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import de.luca.dnd_fight_manager_kmp.Data.paintLoadOverlay
import kotlin.uuid.ExperimentalUuidApi
import de.luca.dnd_fight_manager_kmp.Data.paintSaveOverlay
import de.luca.dnd_fight_manager_kmp.GroupManager.currentIndex
import de.luca.dnd_fight_manager_kmp.GroupManager.currentRound

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun App(title: MutableState<String>) {
    MaterialTheme {
        val currentListName = remember { mutableStateOf("encounter_1") }

        LaunchedEffect(currentListName.value) {
            title.value = "DnD-Fight-Manager-KMP - ${currentListName.value}"
        }

        Box(
            Modifier.blur(if(Overlay.isActive) 3.dp else 0.dp)
        ) {
            Box(Modifier.background(Color.Gray).fillMaxSize())
            Column(Modifier.fillMaxSize()) {
                topBar(currentListName)

                fightersList(Modifier.weight(1f))

                bottomBar()
            }

        }

        Overlay.activeOverlay.value?.let { overlayContent ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .onClick {}
                )
                overlayContent()
            }
        }
    }
}

@Composable
fun topBar(currentListName: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Group management
        Row(Modifier
            .padding(horizontal = 5.dp)
            .background(Color.DarkGray, RoundedCornerShape(10.dp))
        ) {
            Button(
                onClick = { Overlay.showAddGroupOverlay() },
                content = { Text("+ Gruppe") },
                modifier = Modifier.padding(5.dp)
            )
            Button(
                onClick = { Overlay.showAllGroupsOverlay() },
                content = { Text("Gruppen") },
                modifier = Modifier.padding(5.dp)
            )
        }

        // Fighter management
        Row(Modifier
            .padding(horizontal = 5.dp)
            .background(Color.DarkGray, RoundedCornerShape(10.dp))
        ) {
            Button(
                onClick = { GroupManager.freeGroup.value.addFighter(Fighter(mutableStateOf("Fighter-${GroupManager.fighters.size}"))) },
                content = { Text("+ Kämpfer") },
                modifier = Modifier.padding(5.dp)
            )
            Button(
                onClick = { GroupManager.fighters.sortByDescending { it.initiative.value } },
                content = { Text("Sortieren") },
                modifier = Modifier.padding(5.dp)
            )
        }

        // Data management
        Row(Modifier
            .padding(horizontal = 5.dp)
            .background(Color.DarkGray, RoundedCornerShape(10.dp))
        ) {
            Button(
                onClick = { Overlay.showOverlay({ paintSaveOverlay({ Overlay.closeOverlay() }, currentListName) }) },
                content = { Text("Speichern") },
                modifier = Modifier.padding(5.dp)
            )
            Button(
                onClick = { Overlay.showOverlay({ paintLoadOverlay({ Overlay.closeOverlay() }, currentListName) }) },
                content = { Text("Laden") },
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun fightersList(modifier: Modifier) {

    Box(modifier) {
        Box {
            val listState = rememberLazyListState()

            LaunchedEffect(currentIndex) {
                listState.animateScrollToItem(currentIndex)
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(15.dp))
                    .padding(0.dp, 10.dp, 0.dp, 10.dp),
                state = listState
            ) {
                itemsIndexed(
                    items = GroupManager.fighters,
                    key = { _, fighter: Fighter -> fighter.id }
                ) { index: Int, fighter: Fighter ->
                    Box(Modifier.animateItem()) {
                        GroupManager.fighters[index].paintListElement(index, isCurrent = (index == currentIndex))
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
            )
        }
    }
}

@Composable
fun bottomBar() {
    Row(Modifier.fillMaxWidth()) {
        Row(
            Modifier.weight(3f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            extraMenusPopup()
        }

        Row(
            Modifier.weight(5f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            currentFighterManagement()
        }

        Row(
            Modifier.weight(3f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            currentRoundManagement()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun extraMenusPopup() {
    val expanded = remember { mutableStateOf(false) }
    val showFighterPopup = remember { mutableStateOf(false) }

    if(showFighterPopup.value) copyFighterPopUp(showFighterPopup)

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        Button(content = {Text("Extra Funktionen")}, onClick = {})
        ExposedDropdownMenu(
            modifier = Modifier.width(250.dp),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            // Copy Fighter
            DropdownMenuItem(
                onClick = {
                    println("Copy fighter") // open popup instead of overlay with only scrollable list of all fighters and a copy button next to them
                    showFighterPopup.value = true
                    expanded.value = false
                }
            ) {
                Row {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(15.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(vertical = 20.dp)
                    )
                    Text(modifier = Modifier.padding(5.dp), text = "Kämpfer kopieren")
                }
            }
            // Notepad
            DropdownMenuItem(
                onClick = {
                    println("Notepad") // open popup with text field and buttons to adjust size of the popup. Maybe moveable and anchorable too? Notes are safed per save and integrated into GroupManager, if no notes are in a save file just assume it is ""
                    expanded.value = false
                }
            ) {
                Row {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(15.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(vertical = 20.dp)
                    )
                    Text(modifier = Modifier.padding(5.dp), text = "Notizen öffnen")
                }
            }
            // Templates
            DropdownMenuItem(
                onClick = {
                    println("Templates") // open popup with safe and load button. On safe show list popup with list of all fighters scrollable and clickable. On load show popup with all saves. Saves should be in a new subfolder "templates"
                    expanded.value = false
                }
            ) {
                Row {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(15.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .padding(vertical = 20.dp)
                    )
                    Text(modifier = Modifier.padding(5.dp), text = "Templates verwalten")
                }
            }
        }
    }
}

@Composable
fun copyFighterPopUp(showFighterPopup: MutableState<Boolean>) {
    Popup(
        onDismissRequest = { showFighterPopup.value = false },
        alignment = Alignment.Center
    ) {
        Column(
            Modifier
                .size(300.dp, 400.dp)
                .shadow(5.dp, RoundedCornerShape(10.dp))
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text("Kämpfer kopieren:", Modifier.padding(bottom = 10.dp))

            LazyColumn(Modifier.weight(1f)) {
                items(GroupManager.fighters) { fighter: Fighter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fighter.name.value)
                        Button(
                            onClick = { fighter.group.value.addFighter(fighter.copy()) },
                            content = { Text("Kopieren") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showFighterPopup.value = false },
                content = { Text("Fertig") }
            )        }
    }
}

@Composable
fun currentFighterManagement() {
    // One Index back
    Button(
        onClick = {
            if (currentIndex >= 1) currentIndex--
            else {
                currentIndex = GroupManager.fighters.size - 1
                if(currentRound > 1) currentRound--
            } },
        content = { Text("<-") },
        modifier = Modifier.padding(5.dp)
    )

    // Info
    Text(
        (currentIndex + 1).toString(),
        Modifier.padding(5.dp, 0.dp)
    )
    Text(
        if (GroupManager.fighters.getOrNull(currentIndex) == null) "Liste ist leer" else GroupManager.fighters[currentIndex].name.value,
        Modifier.padding(5.dp, 0.dp)
    )

    // One Index forth
    Button(
        onClick = {
            if (currentIndex + 1 < GroupManager.fighters.size) currentIndex++
            else {
                currentIndex = 0
                currentRound++
            } },
        content = { Text("->") },
        modifier = Modifier.padding(5.dp)
    )
}

@Composable
fun currentRoundManagement() {
    Text("Runden")
    IconButton(
        onClick = { if(currentRound > 1) currentRound-- },
        content = { Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
            contentDescription = "Zurück"
        ) },
        modifier = Modifier.padding(5.dp)
    )
    Text(currentRound.toString())
    IconButton(
        onClick = { currentRound++ },
        content = { Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = "Vorwärts"
        ) },
        modifier = Modifier.padding(5.dp)
    )
}