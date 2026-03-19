package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.uuid.ExperimentalUuidApi

object GroupManager {
    var groups by mutableStateOf<PersistentList<Group>>(persistentListOf())
        private set

    private fun remove(group: Group) {
        groups = groups.remove(group)
    }

    fun add(group: Group) {
        groups = groups.add(group)
    }

    fun addAll(elements: Collection<Group>) {
        groups = groups.addAll(elements)
    }

    var currentIndex by mutableStateOf(0)
    var currentRound by mutableStateOf(1)
    var notepad by mutableStateOf("")

    @OptIn(ExperimentalUuidApi::class)
    val freeGroup = mutableStateOf(Group(name=mutableStateOf("Keine"), color=mutableStateOf(Color.White)))
    val fighters = getAllFighters()

    private fun getAllFighters(): SnapshotStateList<Fighter> {
        val fighters = mutableStateListOf<Fighter>()

        for(fighter in freeGroup.value.fighters.value) {
            fighters.add(fighter)
        }

        for(group in groups) {
            for(fighter in group.fighters.value) {
                fighters.add(fighter)
            }
        }

        return fighters
    }

    fun deleteGroupWithoutFighters(group: Group) {
        group.transferAllFightersToFreeGroup()
        remove(group)

        println("Removed group ${group.name.value}")
    }

    fun deleteGroupWithAllFighters(group: Group) {
        val fighterCount = group.fighters.value.size

        group.deleteAllFighters()
        remove(group)

        println("Removed group ${group.name.value} and all $fighterCount fighters in it")
    }

    fun deleteEverything() {
        groups.forEach { deleteGroupWithAllFighters(it) }
        freeGroup.value.deleteAllFighters()
    }

    fun transferFighter(fromGroup: Group, toGroup: Group, fighter: Fighter) {
        fromGroup.deleteFighter(fighter)
        toGroup.addFighter(fighter)
    }
}