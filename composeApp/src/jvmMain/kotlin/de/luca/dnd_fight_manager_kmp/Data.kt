package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.uuid.ExperimentalUuidApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

object Data {
    private val userHome = System.getProperty("user.home")
    private val folder = Paths.get(userHome).resolve("DnD-Fight-Manager-KMP")

    private const val SEPARATOR = ";;"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun paintSaveOverlay(fighters: MutableList<Fighter>, onClose: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center
        ){
            Box(
                Modifier
                    .fillMaxSize()
                    .onClick {}
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Box(
                Modifier
                    .size(500.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Speicherort: $folder")

                    val fileName = remember { mutableStateOf("encounter_1") }

                    Spacer(modifier = Modifier.height(10.dp))

                    textField(fileName, "Dateiname (ohne .txt)")

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Button(
                            onClick = {
                                save(fighters, fileName.value)
                                onClose()
                            },
                            content = { Text("Speichern") },
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Button(
                            onClick = { onClose() },
                            content = { Text("Abbrechen") }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun paintLoadOverlay(
        currentFighters: MutableList<Fighter>,
        onClose: () -> Unit
    ) {
        val fileList = remember { getAvailableFiles() }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .onClick {}
            )

            Box(
                Modifier
                    .size(500.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Lade Datei aus: $folder", modifier = Modifier.padding(bottom = 10.dp))

                    if (fileList.isEmpty()) {
                        Text("Keine Dateien gefunden.", color = Color.Gray)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(5.dp))
                        ) {
                            items(fileList) { fileName ->
                                var isHovered by remember { mutableStateOf(false) }
                                val shadow by animateDpAsState(if(isHovered) 5.dp else 0.dp, tween(200))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onClick {
                                            val loadedFighters = load(fileName)
                                            if (loadedFighters.isNotEmpty()) {
                                                currentFighters.clear()
                                                currentFighters.addAll(loadedFighters)
                                                onClose()
                                            }
                                        }
                                        .padding(5.dp)
                                        .shadow(shadow)
                                        .background(Color.LightGray, RoundedCornerShape(10.dp))
                                        .padding(5.dp)
                                        .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                        .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                                ) {
                                    Text("📄 $fileName")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onClose() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Abbrechen")
                    }
                }
            }
        }
    }

    private fun getAvailableFiles(): List<String> {
        secureFolder()
        val files = folder.toFile().listFiles()
        return files
            ?.filter { it.isFile && it.name.endsWith(".txt") }
            ?.map { it.name.removeSuffix(".txt") }
            ?.sorted()
            ?: emptyList()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun save(fighters: MutableList<Fighter>, fileName: String) {
        secureFolder()

        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()

        try {
            file.printWriter().use { out ->
                fighters.forEach { fighter ->
                    val name = fighter.name.value.replace(SEPARATOR, " ")
                    val info = fighter.extraInfo.value.replace(SEPARATOR, " ")
                    val init = fighter.initiative.value.toString()
                    val id = fighter.id.toString()

                    val line = "$id$SEPARATOR$name$SEPARATOR$info$SEPARATOR$init"
                    out.println(line)
                }
            }
            println("Erfolgreich gespeichert unter: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Fehler beim Speichern: ${e.message}")
        }
    }

    private fun secureFolder() {
        if (!folder.exists()) {
            folder.toFile().mkdirs()
            println("Ordner erstellt: $folder")
        }
    }

    fun load(fileName: String): List<Fighter> {
        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()
        val loadedList = mutableListOf<Fighter>()

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(SEPARATOR)
                if (parts.size >= 4) {
                    val name = parts[1]
                    val info = parts[2]
                    val init = parts[3].toIntOrNull() ?: 0

                    val fighter = Fighter(
                        name = mutableStateOf(name),
                        extraInfo = mutableStateOf(info),
                        initiative = mutableStateOf(init)
                    )
                    loadedList.add(fighter)
                }
            }
        }
        return loadedList
    }
}