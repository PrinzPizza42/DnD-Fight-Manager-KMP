package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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
    fun paintListElement(index: Int, isCurrent: Boolean) {
        MaterialTheme {
            var isHovered by remember { mutableStateOf(false) }
            val shadow by animateDpAsState(if(isHovered && !isCurrent) 15.dp else 0.dp, tween(200))
            val backGroundColor = if(isCurrent) lerp(Color.Transparent, Color.hsl(256f, 0.34f, 0.48f), 0.7f) else Color.LightGray
            val animatedBackGroundColor by animateColorAsState(
                targetValue = backGroundColor,
                tween(200)
            )
            val borderStroke = if (isCurrent) BorderStroke(2.dp, lerp(start = Color.Transparent, stop = Color.Magenta, 0.8f)) else null

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .then(if(borderStroke != null) Modifier.border(borderStroke, RoundedCornerShape(10.dp)) else Modifier)
                    .shadow(shadow, shape = RoundedCornerShape(10.dp))
                    .background(animatedBackGroundColor, RoundedCornerShape(10.dp))
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
                                value = "${group.value.name.value[0]}${if(group.value.name.value.length >= 2) group.value.name.value[1] else ""}",
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
                    onClick = { group.value.deleteFighter(this@Fighter) },
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
                GroupManager.transferFighter(this@Fighter.group.value, group, this@Fighter)
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
}
