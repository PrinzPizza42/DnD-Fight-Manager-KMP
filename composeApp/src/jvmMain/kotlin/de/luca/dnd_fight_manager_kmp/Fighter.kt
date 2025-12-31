package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class Fighter(
    var name: MutableState<String> = mutableStateOf("Name"),
    var extraInfo: MutableState<String> = mutableStateOf("Info"),
    var initiative: MutableState<Int> = mutableStateOf(1)
) {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun paintListElement(removeFighter: (Fighter) -> Boolean, index: Int) {
        MaterialTheme {
            var isHovered by remember { mutableStateOf(false) }
            val shadow by animateDpAsState(if(isHovered) 15.dp else 0.dp, tween(200))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .shadow(shadow)
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                    .padding(5.dp)
                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                    .onPointerEvent(PointerEventType.Exit) { isHovered = false },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}", modifier = Modifier.width(35.dp), textAlign = TextAlign.Center)
                Box(modifier = Modifier.width(200.dp)) {textField(name, "Name:")}
                Box(modifier = Modifier.weight(1f)) {textField(extraInfo, "Info:")}
                Box(modifier = Modifier.width(60.dp)) {textFieldInt(initiative, "Initiative:")}
                Button(
                    onClick = { removeFighter(this@Fighter) },
                    content = { Text("Entfernen", color = Color.White) },
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}
