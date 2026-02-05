package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Fighter(
    var name: MutableState<String> = mutableStateOf("Name"),
    var extraInfo: MutableState<String> = mutableStateOf("Info"),
    var initiative: MutableState<Int> = mutableStateOf(1),
    var group: MutableState<Group> = GroupManager.freeGroup
) {
    @OptIn(ExperimentalUuidApi::class)
    val id = Uuid.random()

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class, ExperimentalUuidApi::class)
    @Composable
    fun paintListElement(index: Int) {
        MaterialTheme {
            var isHovered by remember { mutableStateOf(false) }
            val shadow by animateDpAsState(if(isHovered) 15.dp else 0.dp, tween(200))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .shadow(shadow, shape = RoundedCornerShape(10.dp))
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(5.dp)
                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                    .onPointerEvent(PointerEventType.Exit) { isHovered = false },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .fillMaxHeight()
                        .background(group.value.color.value, RoundedCornerShape(5.dp))
                ) {
                    val expanded = remember { mutableStateOf(false) }

                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = {
                                expanded.value = !expanded.value
                            }
                        ) {
                            TextField(
                                modifier = Modifier.width(55.dp),
                                readOnly = true,
                                value = "${group.value.name.value[0]}${group.value.name.value[1]}",
                                onValueChange = {},
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )
                            ExposedDropdownMenu(
                                modifier = Modifier.width(200.dp),
                                expanded = expanded.value,
                                onDismissRequest = {
                                    expanded.value = false
                                }
                            ) {
                                dropdownMenuGroupElement(GroupManager.freeGroup.value, expanded)

                                GroupManager.groups.forEach { selectionOption ->
                                    if(selectionOption.uuid.value != group.value.uuid.value) {
                                        dropdownMenuGroupElement(selectionOption, expanded)
                                    }
                                }
                            }
                        }
                    }
                }
                Text("${index + 1}", modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                Box(modifier = Modifier.width(200.dp)) {textField(name, "Name:")}
                Box(modifier = Modifier.weight(1f)) {textField(extraInfo, "Info:")}
                Box(modifier = Modifier.width(60.dp)) {textFieldInt(initiative, "Initiative:")}
                Button(
                    onClick = { delete() },
                    content = { Text("Entfernen", color = Color.White) },
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }

    @Composable
    fun dropdownMenuGroupElement(group: Group, expanded: MutableState<Boolean>) {
        DropdownMenuItem(
            onClick = {
                this@Fighter.group.value.removeFighter(this@Fighter)
                group.addFighter(this@Fighter)
                this@Fighter.group.value = group

                expanded.value = false
            }
        ) {
            Row {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .width(15.dp)
                        .background(group.color.value, RoundedCornerShape(4.dp))
                        .padding(vertical = 20.dp)
                )
                Text(modifier = Modifier.padding(5.dp), text = group.name.value)
            }
        }
    }

    fun delete() {
        group.value.removeFighter(this)
    }
}
