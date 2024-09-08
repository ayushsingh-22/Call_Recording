package com.example.call_recording.Utility

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StartRecording(private val context: Context) {
    private lateinit var recorder: MediaRecorder
    private var isRecording = false
    private var outputFilePath: String? = null

    fun startRecording() {
        if (!isRecording) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "RECORDING_$timestamp.3gp"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            outputFilePath = File(storageDir, fileName).absolutePath

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFilePath)
                try {
                    prepare()
                    start()
                    isRecording = true
                    Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun stopRecording() {
        if (isRecording) {
            try {
                recorder.stop()
                recorder.reset()
                recorder.release()
                isRecording = false
                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalStateException) {
                Toast.makeText(context, "Failed to stop recording: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getOutputFilePath(): String? {
        return outputFilePath
    }
}
