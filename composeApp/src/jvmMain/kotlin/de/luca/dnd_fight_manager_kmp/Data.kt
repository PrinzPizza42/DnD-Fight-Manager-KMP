package de.luca.dnd_fight_manager_kmp

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    private val globalSettingsFolder = folder.resolve("global_settings")
    private const val templateFileName = "templates.txt"

    const val SEPARATOR = ";;"
    private const val GROUP_START = "{{"
    private const val GROUP_END = "}}"
    private const val COLOR_SEPARATOR = ","
    private const val notepadSTART = "Notepad:"

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

                textField(fileName, "Dateiname (ohne .txt)", Modifier.fillMaxWidth())

                Box(Modifier.weight(1f))
                Row(modifier = Modifier.padding(top = 10.dp)) {
                    Button(
                        onClick = {
                            save(fileName.value)
                            currentListName.value = fileName.value
                            onClose()
                        },
                        content = { Text("Speichern") },
                        modifier = Modifier.padding(5.dp)
                    )
                    Box(Modifier.weight(1f))
                    Button(
                        onClick = { onClose() },
                        Modifier.padding(5.dp),
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
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Keine Dateien gefunden.", color = Color.Gray)
                    }
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
                                        val freeGroup = loadedGroups[0]
                                        loadedGroups.remove(freeGroup)
                                        currentListName.value = fileName.removeSuffix(".txt")
                                        loadedGroups.isNotEmpty()
                                        GroupManager.deleteEverything()
                                        GroupManager.freeGroup.value = freeGroup
                                        GroupManager.addAll(loadedGroups)
                                        onClose()

                                        GroupManager.currentIndex = 0
                                        GroupManager.currentRound = 1
                                    }
                                    .padding(5.dp)
                                    .shadow(shadow, shape = RoundedCornerShape(10.dp))
                                    .background(Color.LightGray, RoundedCornerShape(10.dp))
                                    .padding(5.dp)
                                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                    .onPointerEvent(PointerEventType.Exit) { isHovered = false },
                                Arrangement.Center,
                                Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.FileOpen, contentDescription = "File", Modifier.padding(horizontal = 10.dp))
                                Text(fileName)
                                Box(Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        removeFile(fileName)
                                        fileList.remove(fileName)
                                    },
                                    content = { Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Löschen"
                                    ) },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                Row {
                    Box(Modifier.weight(1f))
                    Button(
                        onClick = { onClose() },
                        Modifier.padding(5.dp),
                        content = { Text("Abbrechen") },
                    )
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
        secureFolders()
        val files = folder.toFile().listFiles()
        return files
            ?.filter { it.isFile && it.name.endsWith(".txt") }
            ?.map { it.name.removeSuffix(".txt") }
            ?.sorted()
            ?: listOf()
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun save(fileName: String) {
        secureFolders()

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

                // Notepad always last
                out.println(notepadSTART)
                GroupManager.notepad.lines().forEach { line ->
                    out.println("= $line")
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

        out.println(groupLine)
        out.println(GROUP_START)

        group.fighters.value.forEach { fighter ->
            out.println(fighter.toString())
        }

        out.println(GROUP_END)
    }

    private fun secureFolders() {
        if (!folder.exists()) {
            folder.toFile().mkdirs()
            println("Ordner erstellt: $folder")
        }
        if(!globalSettingsFolder.exists()) {
            globalSettingsFolder.toFile().mkdirs()
            println("Ordner erstellt: $globalSettingsFolder")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun load(fileName: String): MutableList<Group> {
        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()

        val groupList = mutableListOf<Group>()

        var currentGroup: Group? = null
        var notepad = ""
        var inNotepad = false

        if (file.exists()) {
            file.forEachLine { line ->
                if(inNotepad) {
                    try {
                        notepad = "$notepad\n${line.subSequence(2, line.length)}"
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                        println("Could not read line: $line")
                    }
                }
                else {
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
                                notepadSTART -> {
                                    inNotepad = true
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
                                currentGroup!!.addFighter(getFighterFromLine(line))
                            }
                        }
                    }
                }
            }
        }

        GroupManager.notepad = notepad
        println("Loaded ${groupList.size} groups")
        return groupList
    }

    fun loadTemplates(): MutableList<Fighter> {
        secureFolders()

        val templates = mutableListOf<Fighter>()
        val file = globalSettingsFolder.resolve(templateFileName).toFile()

        if(!file.exists()) println("Could not load templates because file does not exist")
        else {
            file.forEachLine { line ->
                try {
                    templates.add(getFighterFromLine(line))
                } catch (e: Exception) {
                    println("Could not create fighter from line: $line")
                    e.printStackTrace()
                }
            }
        }

        return templates
    }

    @OptIn(ExperimentalUuidApi::class)
    fun saveTemplates(templates:List<Fighter>) {
        secureFolders()

        val file = globalSettingsFolder.resolve(templateFileName).toFile()

        try {
            file.printWriter().use { out ->
                templates.forEach { fighter ->
                    out.println(fighter.toString())
                }
            }
            println("Erfolgreich templates gespeichert unter: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Fehler beim Speichern: ${e.message}")
        }
    }

    fun getFighterFromLine(line: String): Fighter {
        val parts = line.split(SEPARATOR)

        val name = parts[1]
        val info = parts[2]
        val init = parts[3].toIntOrNull() ?: 0

        return Fighter(
            name = mutableStateOf(name),
            extraInfo = mutableStateOf(info),
            initiative = mutableStateOf(init)
        )
    }
}