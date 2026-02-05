package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Group @OptIn(ExperimentalUuidApi::class) constructor(
    val name: MutableState<String> = mutableStateOf("Name"),
    val fighters: MutableList<Fighter> = mutableListOf(),
    val color: MutableState<Color> = mutableStateOf(Color.random()),
    val uuid: MutableState<Uuid> = mutableStateOf(Uuid.random())
) {
    fun removeFighter(fighter: Fighter) {
        fighters.remove(fighter)
        GroupManager.fighters.remove(fighter)

        println("Removed ${fighter.name} from $name")
    }

    fun addFighter(fighter: Fighter) {
        fighters.add(fighter)
        fighter.group = mutableStateOf(this@Group)
        GroupManager.fighters.add(fighter)

        println("Added ${fighter.name} to $name")
    }
}
