package de.luca.dnd_fight_manager_kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object Overlay {
    val activeOverlay = mutableStateOf<(@Composable () -> Unit)?>(null)
    val isActive: Boolean = activeOverlay.value != null

    fun showOverlay(content: @Composable () -> Unit) {
        activeOverlay.value = content
    }

    fun closeOverlay() { activeOverlay.value = null }


    fun showAddGroupOverlay() {
        showOverlay({
            val name = remember { mutableStateOf("") }

            textField(name, "Name")
        })
    }
}