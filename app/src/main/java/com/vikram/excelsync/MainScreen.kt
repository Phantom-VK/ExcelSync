/**
 * ExcelSync UI Components
 *
 * This file contains the Jetpack Compose UI implementation for the Excel upload screen
 * and related components.
 */
package com.vikram.excelsync

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Main screen for Excel file upload functionality
 *
 * @param viewModel ViewModel handling the upload logic
 * @param onNavigateBack Callback for handling back navigation
 */
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
        // Top app bar with navigation
        TopAppBar(
            title = { Text("Upload Booth Data") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        )

        // Instructions card showing required Excel format
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

        // File selection button
        Button(
            onClick = { showFilePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Excel File")
        }

        // Upload status display
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
                    // Initial state, no status to show
                }
            }
        }
    }

    // File picker launcher
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

/**
 * Composable that launches the system file picker for Excel files
 *
 * @param type MIME type of the file to select
 * @param onResult Callback with the selected file's URI
 */
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