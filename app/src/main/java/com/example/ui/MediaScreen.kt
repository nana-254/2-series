package com.example.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    
    var prompt by remember { mutableStateOf("A beautiful blue BMW M240i driving on a mountain road at sunset") }
    var aspect by remember { mutableStateOf("16:9") }
    var size by remember { mutableStateOf("1K") }
    
    val aspectRatios = listOf("1:1", "2:3", "3:2", "3:4", "4:3", "9:16", "16:9", "21:9")
    val sizes = listOf("1K", "2K", "4K")
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Generate Car Visuals", style = MaterialTheme.typography.headlineMedium)
            
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Prompt") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
        
        item {
            Text("Aspect Ratio", style = MaterialTheme.typography.titleMedium)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = aspect,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    aspectRatios.forEach { ratio ->
                        DropdownMenuItem(
                            text = { Text(ratio) },
                            onClick = { aspect = ratio; expanded = false }
                        )
                    }
                }
            }
        }
        
        item {
            Text("Image Size", style = MaterialTheme.typography.titleMedium)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = size,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    sizes.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = { size = s; expanded = false }
                        )
                    }
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.generateImage(prompt, aspect, size) },
                    enabled = !state.isMediaLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Generate Image")
                }
                
                Button(
                    onClick = { viewModel.generateVideo(prompt, aspect) },
                    enabled = !state.isMediaLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Generate Video")
                }
            }
        }
        
        item {
            if (state.isMediaLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            if (state.mediaError != null) {
                Text(state.mediaError!!, color = MaterialTheme.colorScheme.error)
            }
            if (state.generatedVideoUrl != null) {
                Text(state.generatedVideoUrl!!)
            }
            if (state.generatedImageBase64 != null) {
                val imageBytes = Base64.decode(state.generatedImageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Generated Car Image",
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                }
            }
        }
    }
}
