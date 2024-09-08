package com.example.call_recording

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.call_recording.Utility.StartRecording
import com.example.call_recording.ui.theme.Call_RecordingTheme
import com.example.call_recording.ui.theme.HomeScreenDesign
import java.io.File


class MainActivity : ComponentActivity() {
    private lateinit var startRecording: StartRecording
    private val recordings = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Call_RecordingTheme {
                val homeScreenDesign = HomeScreenDesign()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        homeScreenDesign.Design(
                            modifier = Modifier.padding(innerPadding),
                            recordings = recordings
                        ) { filePath ->
                            openRecording(this, filePath)
                        }
                    }
                )
            }
        }

        startRecording = StartRecording(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), REQUEST_CODE)
        }

        val filter = IntentFilter().apply {
            addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        }
        registerReceiver(CallReceiver(), filter)

        loadRecordings()
    }

    companion object {
        private const val REQUEST_CODE = 200
    }

    private fun loadRecordings() {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val files = storageDir?.listFiles { file -> file.extension == "3gp" }
        recordings.clear()
        files?.forEach { file ->
            recordings.add(file.absolutePath)
        }
    }

    private fun openRecording(context: Context, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "audio/3gpp")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            try {
                context.startActivity(Intent.createChooser(intent, "Open with"))
            } catch (e: ActivityNotFoundException) {
                Log.e("OpenRecording", "No app found to open this file", e)
                Toast.makeText(context, "No app found to open this file", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("OpenRecording", "Failed to open file", e)
                Toast.makeText(context, "Failed to open file", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("OpenRecording", "File not found: $filePath")
            Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
        }
    }

    inner class CallReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            when (state) {
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {

                    startRecording.startRecording()
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {

                    startRecording.stopRecording()
                    loadRecordings()
                }
            }
        }
    }

}

@Composable
fun RecordingItem(recording: String, onOpenFile: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = File(recording).name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recording,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onOpenFile(recording) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Open with")
            }
        }
    }
}

