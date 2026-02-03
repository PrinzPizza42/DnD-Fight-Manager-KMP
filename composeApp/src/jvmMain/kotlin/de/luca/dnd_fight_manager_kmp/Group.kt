package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

data class Group(
    val name: MutableState<String> = mutableStateOf("Name"),
    val fighters: MutableList<Fighter> = mutableListOf(),
    val color: MutableState<Color> = mutableStateOf(Color.random())
) {
    fun removeFighter(fighter: Fighter) {
        fighters.remove(fighter)

        println("Removed ${fighter.name} from $name")
    }

    fun addFighter(fighter: Fighter) {
        fighters.add(fighter)
        fighter.group = mutableStateOf(this@Group)

        println("Added ${fighter.name} to $name")
    }
}
