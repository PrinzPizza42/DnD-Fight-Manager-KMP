package de.luca.dnd_fight_manager_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Fighter(
    var name: MutableState<String> = mutableStateOf("Name"),
    var extraInfo: MutableState<String> = mutableStateOf("Info"),
    var initiative: MutableState<Int> = mutableStateOf(1)
) {
    @Composable
    fun paintListElement(removeFighter: (Fighter) -> Boolean, index: Int) {
        MaterialTheme {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(Color.LightGray)
            ) {
                Text("$index", modifier = Modifier.weight(1f))
                Box(modifier = Modifier.weight(1f)) {textField(name, "Name:")}
                Box(modifier = Modifier.weight(1f)) {textField(extraInfo, "Info:")}
                Box(modifier = Modifier.weight(1f)) {textFieldInt(initiative, "Initiative:")}
                Button(
                    onClick = { removeFighter(this@Fighter) },
                    content = { Text("Entfernen", color = Color.White) },
                    modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp)
                )
            }
        }
    }
}
