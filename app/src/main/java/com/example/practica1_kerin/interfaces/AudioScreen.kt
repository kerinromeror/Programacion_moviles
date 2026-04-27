package com.example.practica1_kerin.interfaces
import com.example.practica1_kerin.media.SimpleAudioPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.practica1_kerin.media.SimpleAudioRecorder
import com.example.practica1_kerin.storage.AppFiles
@Composable
fun AudioScreen(){
    val context = LocalContext.current
    var audioFiles by remember { mutableStateOf(AppFiles.getAudioFiles(context)) }
    var selectedFile by remember { mutableStateOf<java.io.File?>(null) }

    val (hasAudioPerm, requestAudioPerm) = rememberAudioPermission()

    var status by remember { mutableStateOf("Listo") }

    val player = remember { SimpleAudioPlayer() }
    val recoder = remember { SimpleAudioRecorder() }

    fun refreshFiles() {
        audioFiles = AppFiles.getAudioFiles(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            recoder.stop()
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Audio - Grabar y reproducir", style = MaterialTheme.typography.bodyMedium)
        Text("Permiso microfono: ${if (hasAudioPerm) "OK" else "NO"}")
        Text("Estado : $status")

        if(!hasAudioPerm){
            Button(onClick = requestAudioPerm) { Text("Pedir permiso de microfono") }
        }else{
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val newFile = AppFiles.newAudioFile(context)
                    player.stop()
                    recoder.start(newFile){status = it}
                    status = "Grabando en ${newFile.name}..."
                },
                    enabled = !recoder.isRecording()) {
                    Text("NUEVA REC")
                }

                Button(
                    onClick = {
                        recoder.stop()
                        status = "Grabación guardada"
                        refreshFiles()
                    }, enabled = recoder.isRecording()
                ) { Text("STOP REC")}
            }

            HorizontalDivider()

            Text("Audios Guardados:", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(audioFiles.withIndex().toList()) { (index, file) ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val dateStr = sdf.format(Date(file.lastModified()))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("ID: $index", style = MaterialTheme.typography.labelSmall)
                        Text("Nombre: ${file.name}", style = MaterialTheme.typography.bodyLarge)
                        Text("Ruta: ${file.parent}", style = MaterialTheme.typography.labelMedium)
                        Text("Fecha: $dateStr", style = MaterialTheme.typography.bodySmall)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                selectedFile = file
                                status = "Preparando ${file.name}..."
                                player.prepareFromFile(
                                    file = file,
                                    onCompleted = {status = "Listo para reproducir"},
                                    onError = {status = it}
                                )
                            }) { Text("Seleccionar") }

                            if (selectedFile == file) {
                                Button(onClick = {
                                    player.play { status = it }
                                    status = "Reproduciendo..."
                                }) { Text("Play") }
                                Button(onClick = { player.pause(); status = "Pausado" }) { Text("Pause") }
                                Button(onClick = { player.stop(); status = "Parado" }) { Text("Stop") }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
