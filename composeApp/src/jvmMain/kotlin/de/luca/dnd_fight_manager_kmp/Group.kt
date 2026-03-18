package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Group @OptIn(ExperimentalUuidApi::class) constructor(
    val name: MutableState<String> = mutableStateOf("Name"),
    var fighters: MutableState<PersistentList<Fighter>> = mutableStateOf(persistentListOf()),
    val color: MutableState<Color> = mutableStateOf(Color.random()),
    val uuid: MutableState<Uuid> = mutableStateOf(Uuid.random())
) {
    private fun fightersRemove(fighter: Fighter) {
        fighters = mutableStateOf(fighters.value.remove(fighter))
    }

    private fun fightersAdd(fighter: Fighter) {
        fighters = mutableStateOf(fighters.value.add(fighter))
    }

    fun transferFighterToFreeGroup(fighter: Fighter) {
        fightersRemove(fighter)
        GroupManager.fighters.remove(fighter)
        GroupManager.freeGroup.value.addFighter(fighter)

        println("Transferred ${fighter.name.value} from ${name.value} to free group")
    }

    fun deleteFighter(fighter: Fighter) {
        fightersRemove(fighter)
        GroupManager.fighters.remove(fighter)

        println("Deleted ${fighter.name.value} from ${name.value}")
    }

    fun addFighter(fighter: Fighter) {
        fightersAdd(fighter)
        GroupManager.fighters.add(fighter)
        fighter.group = mutableStateOf(this@Group)

        println("Added ${fighter.name.value} to ${name.value}")
    }

    fun transferAllFightersToFreeGroup() {
        for (fighter in fighters.value) {
            transferFighterToFreeGroup(fighter)
        }
    }

    fun deleteAllFighters() {
        for (fighter in fighters.value) {
            deleteFighter(fighter)
        }
    }
}
