package de.luca.dnd_fight_manager_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Fighter(
    var name: String = "Name",
    var extraInfo: String = "Info",
    var initiative: Int = 1
) {
    @Composable
    fun paintListElement(removeFighter: (Fighter) -> Boolean, index: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(Color.LightGray)
        ) {
            Text("$index", modifier = Modifier.weight(1f))
            Box(modifier = Modifier.weight(1f)) {textField({name = it}, name, "Name:")}
            Box(modifier = Modifier.weight(1f)) {textField({extraInfo = it}, extraInfo, "Info:")}
            Box(modifier = Modifier.weight(1f)) {textFieldInt({initiative = it}, initiative, "Initiative:")}
            Button(
                onClick = { removeFighter(this@Fighter) },
                content = { Text("Entfernen") },
                modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp)
            )
        }
    }
}
