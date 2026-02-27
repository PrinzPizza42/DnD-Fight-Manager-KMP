package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import java.io.PrintWriter

object Data {
    private val userHome = System.getProperty("user.home")
    private val folder = Paths.get(userHome).resolve("DnD-Fight-Manager-KMP")

    private const val SEPARATOR = ";;"
    private const val GROUP_START = "{{"
    private const val GROUP_END = "}}"
    private const val COLOR_SEPARATOR = ","

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun paintSaveOverlay(onClose: () -> Unit, currentListName: MutableState<String>) {
        Box(
            Modifier
                .size(500.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(20.dp)
        ) {
            Column {
                Text("Speicherort: $folder")

                val fileName = remember { mutableStateOf(currentListName.value) }

                Spacer(modifier = Modifier.height(10.dp))

                textField(fileName, "Dateiname (ohne .txt)")

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    Button(
                        onClick = {
                            save(fileName.value)
                            currentListName.value = fileName.value
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

    @OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
    @Composable
    fun paintLoadOverlay(
        onClose: () -> Unit,
        currentListName: MutableState<String>
    ) {
        val fileList = remember { getAvailableFiles().toMutableStateList() }

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
                                    .height(70.dp)
                                    .onClick {
                                        val loadedGroups = load(fileName)
                                        currentListName.value = fileName.removeSuffix(".txt")
                                        if (loadedGroups.isNotEmpty()) {
                                            GroupManager.groups.clear()
                                            GroupManager.groups.addAll(loadedGroups)
                                            onClose()
                                        }
                                    }
                                    .padding(5.dp)
                                    .shadow(shadow, shape = RoundedCornerShape(10.dp))
                                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                                    .padding(5.dp)
                                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                    .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                            ) {
                                Text("📄 $fileName", modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        removeFile(fileName)
                                        fileList.remove(fileName)
                                    },
                                    content = { Text("Löschen") },
                                    modifier = Modifier.padding(5.dp)
                                )
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

    private fun removeFile(filename: String) {
        val file = folder.resolve("$filename.txt").toFile()
        if(file.exists()) file.delete()
        println("Removed $filename")
    }

    private fun getAvailableFiles(): List<String> {
        secureFolder()
        val files = folder.toFile().listFiles()
        println("Found files: ${files.size}")
        return files
            ?.filter { it.isFile && it.name.endsWith(".txt") }
            ?.map { it.name.removeSuffix(".txt") }
            ?.sorted()
            ?: listOf()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun save(fileName: String) {
        secureFolder()

        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()

        try {
            file.printWriter().use { out ->
                // Free Group always first
                saveGroup(GroupManager.freeGroup.value, out)

                // All other groups
                GroupManager.groups.forEach { group ->
                    saveGroup(group, out)
                }
            }
            println("Erfolgreich gespeichert unter: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Fehler beim Speichern: ${e.message}")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveGroup(group: Group, out: PrintWriter) {
        val name = group.name.value.replace(SEPARATOR, " ")

        var color = ""
        color = "${group.color.value.red}"
        color = "$color$COLOR_SEPARATOR${group.color.value.green}"
        color = "$color$COLOR_SEPARATOR${group.color.value.blue}"
        color = "$color$COLOR_SEPARATOR${group.color.value.alpha}"

        val uuid = group.uuid.value.toString().replace(SEPARATOR, " ")
        val groupLine = "$name$SEPARATOR$color$SEPARATOR$uuid"
        println(groupLine)
        println(GROUP_START)
        out.println(groupLine)
        out.println(GROUP_START)

        group.fighters.forEach { fighter ->
            val name = fighter.name.value.replace(SEPARATOR, " ")
            val info = fighter.extraInfo.value.replace(SEPARATOR, " ")
            val init = fighter.initiative.value.toString()
            val id = fighter.id.toString()

            val fighterLine = "$id$SEPARATOR$name$SEPARATOR$info$SEPARATOR$init"
            out.println(fighterLine)
            println(fighterLine)
        }

        println(GROUP_END)
        out.println(GROUP_END)
    }

    private fun secureFolder() {
        if (!folder.exists()) {
            folder.toFile().mkdirs()
            println("Ordner erstellt: $folder")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun load(fileName: String): MutableList<Group> {
        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()

        val groupList = mutableListOf<Group>()

        var currentGroup: Group? = null

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(SEPARATOR)

                when(parts.size) {
                    // Structure elements
                    1 -> {
                        when(parts[0]) {
                            GROUP_START -> {
                                println("\nGroup load ${currentGroup!!.name.value} start")
                            }
                            GROUP_END -> {
                                if(currentGroup != null) {
                                    println("Group load ${currentGroup!!.name.value} end\n")

                                    groupList.add(currentGroup!!)
                                    currentGroup = null
                                }
                                else {
                                    error("Tried adding currentGroup but it was null, this should not be the case")
                                }
                            }
                        }
                    }

                    // A group
                    else -> {
                        // Group head
                        if(currentGroup == null) {
                            val name = parts[0]
                            val rgba = parts[1].split(COLOR_SEPARATOR)
                            val color = Color(red = rgba[0].toFloat(), green = rgba[1].toFloat(), blue = rgba[2].toFloat(), alpha = rgba[3].toFloat(), colorSpace = ColorSpaces.Srgb)

                            val group = Group(
                                name = mutableStateOf(name),
                                color = mutableStateOf(color)
                            )
                            currentGroup = group
                        }
                        // Group body
                        else {
                            for (part in parts) println("- $part")

                            val name = parts[1]
                            val info = parts[2]
                            val init = parts[3].toIntOrNull() ?: 0

                            val fighter = Fighter(
                                name = mutableStateOf(name),
                                extraInfo = mutableStateOf(info),
                                initiative = mutableStateOf(init)
                            )

                            currentGroup!!.addFighter(fighter)
                        }
                    }
                }
            }
        }

        println("Loaded ${groupList.size} groups")
        return groupList
    }
}