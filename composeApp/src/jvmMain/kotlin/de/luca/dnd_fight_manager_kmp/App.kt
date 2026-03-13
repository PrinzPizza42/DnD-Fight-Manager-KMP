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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.luca.dnd_fight_manager_kmp.Data.paintLoadOverlay
import kotlin.uuid.ExperimentalUuidApi
import de.luca.dnd_fight_manager_kmp.Data.paintSaveOverlay
import de.luca.dnd_fight_manager_kmp.GroupManager.currentIndex

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
    var currentRound by remember { mutableStateOf(1) }

    Row(Modifier.fillMaxWidth()) {
        Row(
            Modifier.weight(3f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //TODO Add extra functions overlay
        }

        Row(
            Modifier.weight(5f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Row(
            Modifier.weight(3f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
    }
}