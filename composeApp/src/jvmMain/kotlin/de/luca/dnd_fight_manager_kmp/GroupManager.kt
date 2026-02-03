package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

object GroupManager {
    val groups = mutableStateListOf<Group>()
    val freeGroup = mutableStateOf(Group(name=mutableStateOf("Keine"), color=mutableStateOf(Color.White)))
    val fighters = getAllFighters()

    fun removeGroup(group: Group) {
        for(fighter in group.fighters) {
            group.removeFighter(fighter)
            freeGroup.value.addFighter(fighter)
        }
        groups.remove(group)

        println("Removed group ${group.name}")
    }

    fun addGroup(group: Group) {
        groups.add(group)
    }

    fun getAllFighters(): SnapshotStateList<Fighter> {
        val fighters = mutableStateListOf<Fighter>()

        for(fighter in freeGroup.value.fighters) {
            fighters.add(fighter)
        }

        for(group in groups) {
            for(fighter in group.fighters) {
                fighters.add(fighter)
            }
        }

        return fighters
    }
}