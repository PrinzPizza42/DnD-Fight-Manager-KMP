package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.delay
import kotlin.random.Random

fun Color.Companion.random(): Color {
    return Color.hsv(
        hue = Random.nextFloat() * 360f,
        saturation = 1f,
        value = 1f
    )
}

@Composable
fun textField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    onEnter: () -> Unit = {},
    focusAllOnSelect: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused) {
        if (isFocused && focusAllOnSelect) {
            delay(70)

            val textLength = value.text.length
            onValueChange(
                value.copy(selection = TextRange(0, textLength))
            )
        }
    }

    MaterialTheme {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Enter, Key.NumPadEnter -> {
                                focusManager.clearFocus()
                                onEnter()
                                true
                            }
                            Key.Escape -> {
                                focusManager.clearFocus()
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                },
            label = { if(label != null) Text(label) },
            singleLine = true
        )
    }
}

@Composable
fun textFieldInt(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String? = null,
    modifier: Modifier = Modifier,
    onEnter: () -> Unit = {},
    focusAllOnSelect: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }

    var isFocused by remember { mutableStateOf(false) }
    LaunchedEffect(isFocused) {
        if (isFocused && value.text.isNotEmpty() && focusAllOnSelect) {
            delay(70)

            val textLength = value.text.length
            onValueChange(
                value.copy(selection = TextRange(0, textLength))
            )
        }
    }

    MaterialTheme {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if(newValue.text.isEmpty()) {
                    isError = false
                    onValueChange(TextFieldValue(0.toString()))
                }
                else if (newValue.text.all { it.isDigit() } && newValue.text.toIntOrNull() != null) {
                    isError = false
                    onValueChange(newValue)
                } else {
                    isError = true
                }
            },
            modifier = modifier
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Enter, Key.NumPadEnter -> {
                                focusManager.clearFocus()
                                onEnter()
                                true
                            }
                            Key.Escape -> {
                                focusManager.clearFocus()
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                },
            isError = isError,
            label = { if(label != null) Text(label) },
            singleLine = true
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun colorElement(
    currentColor: MutableState<Color>,
    showPopup: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val color = remember { mutableStateOf(currentColor.value) }

    var isHovered by remember { mutableStateOf(false) }
    var scale by mutableStateOf(if(isHovered) 1.2f else 1f)
    var shadow by  mutableStateOf(if(isHovered) 5.dp else 2.dp)

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 250)
    )

    val animatedShadow by animateDpAsState(
        targetValue = shadow,
        animationSpec = tween(durationMillis = 250)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .padding(10.dp)
                .scale(animatedScale)
                .shadow(animatedShadow, CircleShape)
                .background(currentColor.value, CircleShape)
                .size(30.dp)
                .onClick { showPopup.value = !showPopup.value }
                .onPointerEvent(eventType = PointerEventType.Enter) { isHovered = true }
                .onPointerEvent(eventType = PointerEventType.Exit) { isHovered = false},
        ) {
            if(showPopup.value) {
                Popup(
                    onDismissRequest = { showPopup.value = false }
                ) {
                    Column(
                        Modifier
                            .shadow(10.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .size(420.dp, 365.dp)
                            .padding(5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Color Picker")
                            Box(Modifier.weight(1f))
                            IconButton(
                                onClick = { showPopup.value = false },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Schließen"
                                    )
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Row {
                            // Color picker
                            HsvColorPicker(color)

                            // Basic colors
                            Column(
                                Modifier.size(150.dp, 150.dp)
                            ) {
                                Row {
                                    standardColorElement(color, Color.White)
                                    standardColorElement(color, Color.Black)
                                    standardColorElement(color, Color.Gray)
                                }
                                Row {
                                    standardColorElement(color, Color.Red)
                                    standardColorElement(color, Color.Green)
                                    standardColorElement(color, Color.Blue)
                                }
                                Row {
                                    standardColorElement(color, Color.Yellow)
                                    standardColorElement(color, Color.Cyan)
                                    standardColorElement(color, Color.Magenta)
                                }
                            }

                            // Color element
                            Box(Modifier
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .background(color.value, RoundedCornerShape(10.dp))
                                .size(100.dp, 150.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    currentColor.value = color.value
                                    showPopup.value = false
                                },
                                content = { Text("Übernehmen") },
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }

        Text("Zum ändern Kreis anklicken")
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun standardColorElement(color: MutableState<Color>, displayColor: Color) {
    var isHovered by remember { mutableStateOf(false) }

    var scale by mutableStateOf(if(isHovered) 1.2f else 1f)
    var shadow by  mutableStateOf(if(isHovered) 5.dp else 2.dp)

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 250)
    )

    val animatedShadow by animateDpAsState(
        targetValue = shadow,
        animationSpec = tween(durationMillis = 250)
    )

    Box(Modifier
        .padding(5.dp)
        .scale(animatedScale)
        .shadow(animatedShadow, CircleShape)
        .background(displayColor, CircleShape)
        .onClick { color.value = displayColor }
        .size(30.dp)
        .onPointerEvent(eventType = PointerEventType.Enter) { isHovered = true }
        .onPointerEvent(eventType = PointerEventType.Exit) { isHovered = false}
    )
}

@Composable
fun HsvColorPicker(color: MutableState<Color>) {
    val initialHsv = remember { color.value.toHsv() }

    var hue by remember { mutableStateOf(initialHsv[0]) }
    var saturation by remember { mutableStateOf(initialHsv[1]) }
    var value by remember { mutableStateOf(initialHsv[2]) }

    LaunchedEffect(color.value) {
        val currentSliderColor = Color.hsv(hue, saturation, value)
        if (currentSliderColor != color.value) {
            val hsv = color.value.toHsv()
            hue = hsv[0]
            saturation = hsv[1]
            value = hsv[2]
        }
    }
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Hue Slider
            VerticalSliderColumn(
                label = "H",
                value = hue,
                valueRange = 0f..360f,
                onValueChange = {
                    hue = it
                    color.value = Color.hsv(hue = hue, saturation = saturation, value = value)
                }
            )

            // Saturation Slider
            VerticalSliderColumn(
                label = "S",
                value = saturation,
                valueRange = 0f..1f,
                onValueChange = {
                    saturation = it
                    color.value = Color.hsv(hue = hue, saturation = saturation, value = value)
                }
            )

            // Value Slider
            VerticalSliderColumn(
                label = "V",
                value = value,
                valueRange = 0f..1f,
                onValueChange = {
                    value = it
                    color.value = Color.hsv(hue = hue, saturation = saturation, value = value)
                }
            )
        }
    }
}

@Composable
fun VerticalSliderColumn(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val sliderHeight = 150.dp
    val sliderWidth = 40.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(sliderWidth)
                .height(sliderHeight),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier
                    .requiredWidth(sliderHeight)
                    .rotate(-90f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label)
        Text(if(value <= 1) String.format("%.2f", value) else String.format("%.0f", value))
    }
}

fun Color.toHsv(): FloatArray {
    val r = this.red
    val g = this.green
    val b = this.blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    var h = 0f
    if (delta != 0f) {
        h = when (max) {
            r -> 60f * (((g - b) / delta) % 6f)
            g -> 60f * (((b - r) / delta) + 2f)
            b -> 60f * (((r - g) / delta) + 4f)
            else -> 0f
        }
        if (h < 0f) h += 360f
    }

    val s = if (max == 0f) 0f else delta / max
    val v = max

    return floatArrayOf(h, s, v)
}

@Composable
fun openAsWindowIconButton(onClick: () -> Unit) {
    IconButton(
        onClick = {
            onClick()
        },
        content = {
            Icon(
                imageVector = Icons.Default.OpenInFull,
                contentDescription = "Open as window"
            )
        }
    )
}