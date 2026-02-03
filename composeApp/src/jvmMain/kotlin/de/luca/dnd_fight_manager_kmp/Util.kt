package de.luca.dnd_fight_manager_kmp

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun Color.Companion.random(): Color {
    return Color.hsv(
        hue = Random.nextFloat() * 360f,
        saturation = 1f,
        value = 1f
    )
}