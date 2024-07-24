package com.example.call_recording.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import java.io.File

class HomeScreenDesign {

    @Composable
    fun Design(modifier: Modifier = Modifier, recordings: List<String>, onOpenFile: (String) -> Unit) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Red)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Recordings",
                        color = Color.Red,
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Surface(
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                LazyColumn {
                    items(recordings) { recording ->
                        RecordingItem(recording) { filePath ->
                            onOpenFile(filePath)
                        }
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
}
