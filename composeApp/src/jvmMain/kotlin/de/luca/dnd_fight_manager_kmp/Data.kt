package de.luca.dnd_fight_manager_kmp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.uuid.ExperimentalUuidApi

object Data {
    private val userHome = System.getProperty("user.home")
    // Nutzung von resolve ist sicherer als String-Konkatenation
    private val folder = Paths.get(userHome).resolve("DnD-Fight-Manager-KMP")

    // Ein Trennzeichen, das wahrscheinlich nicht im Text vorkommt
    private const val SEPARATOR = ";;"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun paintOverlay(fighters: MutableList<Fighter>, onClose: () -> Unit) {
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
                Column { // Column hinzugefügt für bessere Anordnung
                    Text("Speicherort: $folder")

                    val fileName = remember { mutableStateOf("encounter_1") }

                    // Ein bisschen Abstand
                    Spacer(modifier = Modifier.height(10.dp))

                    textField(fileName, "Dateiname (ohne .txt)")

                    Row(modifier = Modifier.padding(top = 10.dp)) {
                        Button(
                            onClick = {
                                save(fighters, fileName.value)
                                onClose() // Overlay schließen nach Speichern
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

    @OptIn(ExperimentalUuidApi::class)
    private fun save(fighters: MutableList<Fighter>, fileName: String) {
        secureFolder()

        // Sicherstellen, dass die Datei auf .txt endet
        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()

        try {
            // printWriter().use {...} schließt den Stream automatisch
            file.printWriter().use { out ->
                fighters.forEach { fighter ->
                    // Wir holen die Werte (.value) aus den States
                    val name = fighter.name.value.replace(SEPARATOR, " ") // Schutz vor Formatbruch
                    val info = fighter.extraInfo.value.replace(SEPARATOR, " ")
                    val init = fighter.initiative.value.toString()
                    val id = fighter.id.toString()

                    // Schreiben der Zeile: ID;;Name;;Info;;Init
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

    // BONUS: Funktion zum Laden (wirst du wahrscheinlich auch brauchen)
    fun load(fileName: String): List<Fighter> {
        val finalName = if (fileName.endsWith(".txt")) fileName else "$fileName.txt"
        val file = folder.resolve(finalName).toFile()
        val loadedList = mutableListOf<Fighter>()

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(SEPARATOR)
                if (parts.size >= 4) {
                    // ID (parts[0]) ignorieren wir beim Laden für neue Objekte oder nutzen es, wenn Fighter var id hat
                    val name = parts[1]
                    val info = parts[2]
                    val init = parts[3].toIntOrNull() ?: 0

                    // Neuen Fighter erstellen (ID wird neu generiert, oder du passt Fighter an)
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