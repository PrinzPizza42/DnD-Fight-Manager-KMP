package de.luca.dnd_fight_manager_kmp.hotkeys

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

object KeyBinds {
    val menuFocusRequester = FocusRequester()

    // MainMenu
    val lastFighter = mutableStateOf(false)
    val nextFighter = mutableStateOf(false)
    val groupMenu = mutableStateOf(false)
    val addGroupMenu = mutableStateOf(false)
    val notepad = mutableStateOf(false)
    val addFighter = mutableStateOf(false)
    val orderFightersList = mutableStateOf(false)
    val saveMenu = mutableStateOf(false)
    val loadMenu = mutableStateOf(false)
    val templates = mutableStateOf(false)
    val copyCurrentFighter = mutableStateOf(false)
    val deleteCurrentFighter = mutableStateOf(false)
    val deleteEverything = mutableStateOf(false)
    val closeMenu = mutableStateOf(false)
    val helpMenu = mutableStateOf(false)

    fun onKeyPress(event: KeyEvent): Boolean {
        val isAltPressed = event.isAltPressed
        val isShiftPressed = event.isShiftPressed

        when (event.type) {
            KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.DirectionLeft, Key.DirectionUp -> {
                        lastFighter.value = true
                        return true
                    }

                    Key.DirectionRight, Key.DirectionDown -> {
                        nextFighter.value = true
                        return true
                    }

                    Key.G -> {
                        if (isAltPressed) {
                            if(isShiftPressed) addGroupMenu.value = true
                            else groupMenu.value = true
                            return true
                        }
                        return false
                    }

                    Key.N -> {
                        if (isAltPressed) {
                            notepad.value = true
                            return true
                        }
                        return false
                    }

                    Key.F -> {
                        if (isAltPressed) {
                            addFighter.value = true
                            return true
                        }
                        return false
                    }

                    Key.O -> {
                        if (isAltPressed) {
                            orderFightersList.value = true
                            return true
                        }
                        return false
                    }

                    Key.S -> {
                        if (isAltPressed) {
                            saveMenu.value = true
                            return true
                        }
                        return false
                    }

                    Key.L -> {
                        if (isAltPressed) {
                            loadMenu.value = true
                            return true
                        }
                        return false
                    }

                    Key.T -> {
                        if (isAltPressed) {
                            templates.value = true
                            return true
                        }
                        return false
                    }

                    Key.C -> {
                        if (isAltPressed) {
                            copyCurrentFighter.value = true
                            return true
                        }
                        return false
                    }

                    Key.Delete, Key.Backspace -> {
                        if (isAltPressed) {
                            if (isShiftPressed) {
                                de.luca.dnd_fight_manager_kmp.Overlay.showDeleteEverythingOverlay()
                            } else {
                                deleteCurrentFighter.value = true
                            }
                            return true
                        }
                        return false
                    }

                    Key.Escape -> {
                        closeMenu.value = true
                        return true
                    }

                    Key.H -> {
                        helpMenu.value = true
                        return true
                    }

                    else -> return false
                }
            }
            KeyEventType.KeyUp -> {
                when (event.key) {
                    Key.DirectionLeft, Key.DirectionUp -> {
                        lastFighter.value = false
                        return true
                    }

                    Key.DirectionRight, Key.DirectionDown -> {
                        nextFighter.value = false
                        return true
                    }

                    Key.G -> {
                        if(isShiftPressed) addGroupMenu.value = false
                        else groupMenu.value = false
                        return isAltPressed
                    }

                    Key.N -> {
                        notepad.value = false
                        return isAltPressed
                    }

                    Key.F -> {
                        addFighter.value = false
                        return isAltPressed
                    }

                    Key.O -> {
                        orderFightersList.value = false
                        return isAltPressed
                    }

                    Key.S -> {
                        saveMenu.value = false
                        return isAltPressed
                    }

                    Key.L -> {
                        loadMenu.value = false
                        return isAltPressed
                    }

                    Key.T -> {
                        templates.value = false
                        return isAltPressed
                    }

                    Key.C -> {
                        copyCurrentFighter.value = false
                        return isAltPressed
                    }

                    Key.Delete, Key.Backspace -> {
                        if(isShiftPressed) deleteEverything.value = false
                        else deleteCurrentFighter.value = false
                        return isAltPressed
                    }

                    Key.Escape -> {
                        closeMenu.value = false
                        return true
                    }

                    Key.H -> {
                        helpMenu.value = false
                        return true
                    }

                    else -> return false
                }
            }
            else -> return false
        }
    }
}