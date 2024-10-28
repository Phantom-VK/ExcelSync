/**
 * ExcelSync ViewModel and Upload Management
 *
 * This file contains the core business logic for Excel file processing and Firebase upload functionality.
 */
package com.vikram.excelsync

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.IOException
import java.io.InputStream

/**
 * ViewModel responsible for managing Excel file uploads and maintaining UI state.
 *
 * @property context Application context for accessing ContentResolver
 * @property database Firebase Database reference for data upload
 */
class ExcelUploadViewModel(
    private val context: Application,
    database: DatabaseReference
) : AndroidViewModel(context) {

    // Initialize the Excel to Firebase uploader
    private val uploader = ExcelToFirebaseUploader(database)

    // Maintain upload state for UI updates
    private val _uploadState = mutableStateOf<UploadState>(UploadState.Idle)
    val uploadState: State<UploadState> = _uploadState

    /**
     * Initiates the Excel file upload process
     *
     * @param uri URI of the selected Excel file
     */
    fun uploadExcelFile(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading

            try {
                // Open the file input stream
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Cannot open file")

                // Process the upload and handle the result
                when (val result = uploader.uploadExcelToFirebase(inputStream)) {
                    is ExcelToFirebaseUploader.UploadResult.Success -> {
                        _uploadState.value = UploadState.Success(result.uploadedCount)
                    }
                    is ExcelToFirebaseUploader.UploadResult.Error -> {
                        _uploadState.value = UploadState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(
                    "Failed to process file: ${e.message}"
                )
            }
        }
    }
}

/**
 * Handles the actual Excel file processing and Firebase upload operations
 *
 * @property database Firebase Database reference for uploading data
 */
class ExcelToFirebaseUploader(
    private val database: DatabaseReference
) {
    sealed class UploadResult {
        data class Success(val uploadedCount: Int) : UploadResult()
        data class Error(val message: String) : UploadResult()
    }

    /**
     * Processes the Excel file and uploads data to Firebase
     *
     * @param inputStream Input stream of the Excel file
     * @return UploadResult indicating success or failure
     */
    suspend fun uploadExcelToFirebase(inputStream: InputStream): UploadResult {
        return withContext(Dispatchers.IO) {
            try {
                // Create workbook from input stream
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)
                var uploadCount = 0

                // Process each row (skip header)
                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    try {
                        // Extract booth data from row
                        val booth = Booth(
                            name = row.getCell(0)?.stringCellValue ?: "",
                            id = row.getCell(1)?.stringCellValue ?: "",
                            bloName = row.getCell(2)?.stringCellValue ?: "",
                            bloContact = row.getCell(3)?.numericCellValue?.toLong().toString(),
                            city = row.getCell(4)?.stringCellValue ?: "",
                            district = row.getCell(5)?.stringCellValue ?: "",
                            taluka = row.getCell(6)?.stringCellValue ?: "",
                            latitude = row.getCell(7)?.numericCellValue ?: 0.0,
                            longitude = row.getCell(8)?.numericCellValue ?: 0.0,


                            )

                        // Validate required fields
                        if (booth.id.isBlank() || booth.district.isBlank()) {
                            Log.w("ExcelUpload", "Skipping row $rowIndex: Missing required fields")
                            continue
                        }

                        // Upload booth data to Firebase
                        database
                            .child(booth.district)
                            .child(booth.id)
                            .setValue(booth)
                            .await()

                        uploadCount++
                    } catch (e: Exception) {
                        Log.e("ExcelUpload", "Error processing row $rowIndex", e)
                    }
                }

                workbook.close()
                UploadResult.Success(uploadCount)
            } catch (e: Exception) {
                UploadResult.Error("Failed to process Excel file: ${e.message}")
            }
        }
    }
}

/**
 * Represents the various states of the upload process
 */
sealed class UploadState {
    data object Idle : UploadState()           // Initial state
    data object Uploading : UploadState()      // Upload in progress
    data class Success(val count: Int) : UploadState()  // Upload completed successfully
    data class Error(val message: String) : UploadState() // Upload failed
}