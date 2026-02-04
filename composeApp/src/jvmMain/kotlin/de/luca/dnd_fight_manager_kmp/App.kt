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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.luca.dnd_fight_manager_kmp.Data.paintLoadOverlay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import de.luca.dnd_fight_manager_kmp.Data.paintSaveOverlay

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
@Preview
fun App(title: MutableState<String>) {
    MaterialTheme {
        val currentListName = remember { mutableStateOf("encounter_1") }

        LaunchedEffect(currentListName.value) {
            title.value = "DnD-Fight-Manager-KMP - ${currentListName.value}"
        }

        var count = 0

        Box(
            Modifier.blur(if(Overlay.isActive) 3.dp else 0.dp)
        ) {
            Column {
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
                            onClick = { GroupManager.freeGroup.value.addFighter(Fighter(mutableStateOf("Fighter-${count++}"))) },
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
                            onClick = { Overlay.showOverlay({ paintSaveOverlay(GroupManager.fighters, { Overlay.closeOverlay() }, currentListName) }) },
                            content = { Text("Speichern") },
                            modifier = Modifier.padding(5.dp)
                        )
                        Button(
                            onClick = { Overlay.showOverlay({ paintLoadOverlay(GroupManager.fighters, { Overlay.closeOverlay() }, currentListName) }) },
                            content = { Text("Laden") },
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }
                Box {
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
                            state = listState
                        ) {
                            itemsIndexed(
                                items = GroupManager.fighters,
                                key = { _, fighter: Fighter -> fighter.id }
                            ) { index: Int, fighter: Fighter ->
                                Box(Modifier.animateItem()) {
                                    GroupManager.fighters[index].paintListElement(index)
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