package com.example.practica1_kerin.storage

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

    object AppFiles{
        fun audioFile(context: Context): File =
            File(context.filesDir, "grabacion.m4a")

        fun newAudioFile(context: Context): File {
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            return File(context.filesDir, "audio_$ts.m4a")
        }

        fun getAudioFiles(context: Context): List<File> {
            return context.filesDir.listFiles { file ->
                file.name.startsWith("audio_") && file.name.endsWith(".m4a")
            }?.toList() ?: emptyList()
        }

        fun latestPhotoFile(context: Context): File =
            File(context.filesDir, "ultima_foto.jpg")

        fun newPhotoFile(context: Context): File {
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            return File(context.filesDir, "foto_$ts.jpg")
        }

        fun processedPngFile(context: Context): File =
            File(context.filesDir, "foto_procesada.png")

        fun processedJpgFile(context: Context): File =
            File(context.filesDir, "foto_procesada.jpg")
    }
