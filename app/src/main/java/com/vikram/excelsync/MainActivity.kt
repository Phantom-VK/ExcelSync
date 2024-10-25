/**
 * Excel Data Sync Application for Android
 *
 * This application allows users to upload Excel files containing booth data to Firebase Realtime Database.
 * It demonstrates modern Android development practices including:
 * - Jetpack Compose UI
 * - MVVM Architecture
 * - Coroutines for asynchronous operations
 * - Firebase integration
 * - File handling with Apache POI
 *
 * @author Vikramaditya Khupse
 * @version 1.0
 */
package com.vikram.excelsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.vikram.excelsync.ui.theme.ExcelSyncTheme

/**
 * MainActivity serves as the entry point for the Excel Sync application.
 * It initializes the Firebase database connection and sets up the main UI composition.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display for better visual experience
        enableEdgeToEdge()

        // Initialize Firebase Database connection
        val database = Firebase.database

        // Create a reference to the "Cities" node in Firebase
        val cityRef = database.getReference("Cities")

        // Initialize the ViewModel with application context and database reference
        val viewModel = ExcelUploadViewModel(
            context = application,
            database = cityRef
        )

        // Set up the Compose UI with our custom theme
        setContent {
            ExcelSyncTheme {
                // Initialize the main upload screen with the ViewModel
                ExcelUploadScreen(
                    viewModel = viewModel,
                    onNavigateBack = { finish() } // Handle back navigation
                )
            }
        }
    }
}

/**
 * ViewModelFactory for ExcelUploadViewModel (if needed)
 *
 * Pattern is already setup following MVVM architecture but Factory implementation
 * can be added here if dependency injection is needed in the future.
 */

/**
 * Excel File Format Requirements:
 * The Excel file should contain the following columns in order:
 * 1. Name (Booth name)
 * 2. ID (Required, unique identifier)
 * 3. Latitude (Decimal degrees)
 * 4. Longitude (Decimal degrees)
 * 5. District (Required)
 * 6. Taluka (Administrative division)
 * 7. BLO Name (Booth Level Officer name)
 * 8. BLO Contact (Officer contact number)
 */

/**
 * Data Model: Booth
 * This data class should be defined separately and contain:
 * - name: String
 * - id: String
 * - latitude: Double
 * - longitude: Double
 * - district: String
 * - taluka: String
 * - bloName: String
 * - bloContact: String
 */