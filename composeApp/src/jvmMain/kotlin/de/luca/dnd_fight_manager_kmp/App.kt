package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import de.luca.dnd_fight_manager_kmp.Data.paintLoadOverlay
import kotlin.uuid.ExperimentalUuidApi
import de.luca.dnd_fight_manager_kmp.Data.paintSaveOverlay
import de.luca.dnd_fight_manager_kmp.GroupManager.currentIndex
import de.luca.dnd_fight_manager_kmp.GroupManager.currentListName
import de.luca.dnd_fight_manager_kmp.GroupManager.currentRound
import de.luca.dnd_fight_manager_kmp.Overlay.closeOverlay
import kotlinx.coroutines.delay

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun App(title: MutableState<String>) {
    MaterialTheme {
        LaunchedEffect(currentListName.value) {
            title.value = "DnD-Fight-Manager-KMP - ${currentListName.value}"
        }

        val menuFocusRequester = remember { FocusRequester() }

        Box(
            Modifier
                .blur(if(Overlay.isActive) 3.dp else 0.dp)
                .focusRequester(menuFocusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                        println("Arrow Left")
                        KeyBinds.leftArrow.value = !KeyBinds.leftArrow.value
                        true
                    } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                        println("Arrow Right")
                        KeyBinds.rightArrow.value = !KeyBinds.rightArrow.value
                        true
                    } else if (event.type == KeyEventType.KeyDown && event.key == Key.G && event.type == KeyEventType.KeyDown && event.key == Key.AltLeft) {
                        println("Arrow Right")
                        KeyBinds.rightArrow.value = !KeyBinds.rightArrow.value
                        true
                    } else {
                        false
                    }
                }
        ) {
            Box(Modifier.background(Color.Gray).fillMaxSize())
            Column(Modifier.fillMaxSize()) {
                topBar()

                fightersList(Modifier.weight(1f))

                bottomBar()
            }

        }
        LaunchedEffect(Overlay.isActive) {
            println("Request focus")
            menuFocusRequester.requestFocus()
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
fun topBar() {
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
            val menuFocusRequester = remember { FocusRequester() }
            Button(
                onClick = { Overlay.showAddGroupOverlay(menuFocusRequester) },
                content = { Text("+ Gruppe") },
                modifier = Modifier.padding(5.dp)
            )
            Button(
                onClick = { Overlay.showAllGroupsOverlay(menuFocusRequester) },
                content = { Text("Gruppen") },
                modifier = Modifier.padding(5.dp)
            )
            LaunchedEffect(KeyBinds.altG) {
                Overlay.showAllGroupsOverlay(menuFocusRequester)
            }
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
            Modifier.weight(3f).padding(5.dp),
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
    val showNotepadPopup = remember { mutableStateOf(false) }
    val showTemplatesPopup = remember { mutableStateOf(false) }

    if(showFighterPopup.value) copyFighterPopUp(showFighterPopup)
    if(showNotepadPopup.value) notepadPopUp(showNotepadPopup)
    if(showTemplatesPopup.value) templatesPopUp(showTemplatesPopup)

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
                    showNotepadPopup.value = true
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
                    showTemplatesPopup.value = true
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
            // Clear all
            DropdownMenuItem(
                onClick = {
                    expanded.value = false
                    GroupManager.deleteEverything()
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
                    Text(modifier = Modifier.padding(5.dp), text = "Kämpfer- und Gruppenliste leeren")
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Kämpfer kopieren", Modifier.padding(bottom = 10.dp))
                Box(Modifier.weight(1f))
                openAsWindowIconButton({
                    showFighterPopup.value = false

                    WindowManager.openNewWindow(
                        onCloseRequest = {
                            showFighterPopup.value = true
                        },
                        content = {
                            Column(Modifier.padding(start = 10.dp)) {
                                copyFighterContent()
                            }
                        },
                        title = mutableStateOf("Templates")
                    )
                })
                IconButton(
                    onClick = { showFighterPopup.value = false },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen"
                        )
                    },
                    modifier = Modifier.padding(5.dp)
                )
            }

            copyFighterContent()
        }
    }
}

@Composable
fun ColumnScope.copyFighterContent() {
    if (GroupManager.fighters.isEmpty()) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("Keine Kämpfer gefunden", color = Color.Gray)
        }
    } else {
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
                    IconButton(
                        onClick = { fighter.group.value.addFighter(fighter.copy()) },
                        content = {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Kopieren"
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun notepadPopUp(showNotepadPopup: MutableState<Boolean>) {
    var height by remember { mutableStateOf(500.dp) }
    var width by remember { mutableStateOf(500.dp) }
    val focusManager = LocalFocusManager.current

    Popup(
        onDismissRequest = { showNotepadPopup.value = false },
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true)
    ) {
        Column(
            Modifier
                .size(width, height)
                .shadow(5.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                Modifier.padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notizen")
                Box(Modifier.weight(1f))
                openAsWindowIconButton({
                    showNotepadPopup.value = false

                    WindowManager.openNewWindow(
                        onCloseRequest = {
                            showNotepadPopup.value = true
                        },
                        content = {
                            notepadContent(focusManager)
                        },
                        title = mutableStateOf("Notepad")
                    )
                })
                IconButton(
                    onClick = { showNotepadPopup.value = false },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen"
                        )
                    },
                    modifier = Modifier.padding(5.dp)
                )
            }

            notepadContent(focusManager)
        }
    }
}

@Composable
private fun notepadContent(focusManager: FocusManager) {
    OutlinedTextField(
        value = GroupManager.notepad,
        onValueChange = { text ->
            GroupManager.notepad = text
        },
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Escape -> {
                            focusManager.clearFocus()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        singleLine = false
    )
}

@Composable
fun templatesPopUp(showTemplatesPopup: MutableState<Boolean>) {
    val templates = remember { Data.loadTemplates().toMutableStateList() }
    val popupState = remember { mutableStateOf(0) } // 0 = managen, 1 = addTemplate
    var width by remember { mutableStateOf(300.dp) }
    var height by remember { mutableStateOf(400.dp) }

    Popup(
        onDismissRequest = { showTemplatesPopup.value = false },
        alignment = Alignment.Center
    ) {
        Column(
            Modifier
                .size(width, height)
                .shadow(5.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Templates", Modifier.padding(bottom = 8.dp))
                Box(Modifier.weight(1f))
                openAsWindowIconButton({
                    showTemplatesPopup.value = false

                    WindowManager.openNewWindow(
                        onCloseRequest = {
                            showTemplatesPopup.value = true
                        },
                        content = {
                            Column(Modifier.padding(start = 10.dp)) {
                                templatesPopupContent(popupState, templates)
                            }
                        },
                        title = mutableStateOf("Templates")
                    )
                })
                IconButton(
                    onClick = { showTemplatesPopup.value = false },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Schließen"
                        )
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
            templatesPopupContent(popupState, templates)
        }
    }
}

@Composable
private fun ColumnScope.templatesPopupContent(
    popupState: MutableState<Int>,
    templates: SnapshotStateList<Fighter>,
) {
    when (popupState.value) {
        0 -> {
            LazyColumn(Modifier.weight(1f)) {
                items(templates) { fighter: Fighter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fighter.name.value)
                        Box(Modifier.weight(1f))
                        IconButton(
                            onClick = { GroupManager.freeGroup.value.addFighter(fighter.copy()) },
                            content = { Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nutzen"
                            ) },
                            modifier = Modifier.padding(5.dp)
                        )
                        IconButton(
                            onClick = {
                                templates.remove(fighter)
                                Data.saveTemplates(templates)
                            },
                            content = { Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen"
                            ) },
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
            }
        }
        1 -> {
            if(GroupManager.fighters.isEmpty()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keine Kämpfer gefunden", color = Color.Gray)
                }
            }
            else {
                LazyColumn(Modifier.weight(1f)) {
                    items(GroupManager.fighters) { fighter: Fighter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(fighter.name.value)
                            Box(Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    templates.add(fighter)
                                    Data.saveTemplates(templates)
                                    popupState.value = 0
                                },
                                content = { Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Hinzufügen"
                                ) },
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        when (popupState.value) {
            0 -> {
                Button(
                    onClick = { popupState.value = 1 },
                    content = { Text("Neues Template hinzufügen") }
                )
            }
            1 -> {
                Button(
                    onClick = { popupState.value = 0 },
                    content = { Icon(Icons.AutoMirrored.Default.ArrowLeft, "Zurück") }
                )
            }
        }
    }
}

@Composable
fun currentFighterManagement() {
    // One Index back
    LaunchedEffect(KeyBinds.leftArrow.value) {
        if (currentIndex >= 1) currentIndex--
        else {
            currentIndex = GroupManager.fighters.size - 1
            if(currentRound > 1) currentRound--
        }
    }
    Button(
        onClick = {
            if (currentIndex >= 1) currentIndex--
            else {
                currentIndex = GroupManager.fighters.size - 1
                if(currentRound > 1) currentRound--
            }
        },
        content = { Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowLeft,
            contentDescription = "One index back"
        ) },
        modifier = Modifier.padding(5.dp)
    )

    // Info
    if(GroupManager.fighters.getOrNull(currentIndex) != null) {
        Text(
            (currentIndex + 1).toString(),
            Modifier.padding(5.dp, 0.dp),
            fontStyle = FontStyle.Italic,
        )
    }
    Text(
        if (GroupManager.fighters.getOrNull(currentIndex) == null) "Liste ist leer" else GroupManager.fighters[currentIndex].name.value,
        Modifier.padding(5.dp, 0.dp)
    )

    // One Index forth
    LaunchedEffect(KeyBinds.rightArrow.value) {
        if (currentIndex + 1 < GroupManager.fighters.size) currentIndex++
        else {
            currentIndex = 0
            currentRound++
        }
    }
    Button(
        onClick = {
            if (currentIndex + 1 < GroupManager.fighters.size) currentIndex++
            else {
                currentIndex = 0
                currentRound++
            }
        },
        content = { Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowRight,
            contentDescription = "One index forth"
        ) },
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