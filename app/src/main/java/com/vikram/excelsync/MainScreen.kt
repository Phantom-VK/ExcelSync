package com.vikram.excelsync

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelUploadScreen(
    viewModel: ExcelUploadViewModel,
    onNavigateBack: () -> Unit
) {
    var showFilePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Upload Booth Data") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )

        // Instructions Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Excel Sheet Format:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Required columns (in order):")
                Text("• Name")
                Text("• ID (required)")
                Text("• Latitude")
                Text("• Longitude")
                Text("• District (required)")
                Text("• Taluka")
                Text("• BLO Name")
                Text("• BLO Contact")
            }
        }

        // Upload Button
        Button(
            onClick = { showFilePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Excel File")
        }

        // Status Display
        viewModel.uploadState.value.let { state ->
            when (state) {
                is UploadState.Uploading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                    Text("Uploading booth data...")
                }
                is UploadState.Success -> {
                    Text(
                        "Successfully uploaded ${state.count} booths",
                        color = Color.Green,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                is UploadState.Error -> {
                    Text(
                        state.message,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                UploadState.Idle -> {
                    // No status to show
                }
            }
        }
    }

    if (showFilePicker) {
        GetContent(
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            onResult = { uri ->
                uri?.let { viewModel.uploadExcelFile(it) }
                showFilePicker = false
            }
        )
    }
}

@Composable
fun GetContent(
    type: String,
    onResult: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onResult
    )

    LaunchedEffect(Unit) {
        launcher.launch(type)
    }
}