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


class ExcelUploadViewModel(
    private val context: Application,
    database: DatabaseReference
) : AndroidViewModel(context) {

    private val uploader = ExcelToFirebaseUploader(database)

    private val _uploadState = mutableStateOf<UploadState>(UploadState.Idle)
    val uploadState: State<UploadState> = _uploadState

    fun uploadExcelFile(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading

            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Cannot open file")

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



class ExcelToFirebaseUploader(
    private val database: DatabaseReference
) {
    sealed class UploadResult {
        data class Success(val uploadedCount: Int) : UploadResult()
        data class Error(val message: String) : UploadResult()
    }

    suspend fun uploadExcelToFirebase(inputStream: InputStream): UploadResult {
        return withContext(Dispatchers.IO) {
            try {
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)
                var uploadCount = 0

                // Skip header row, start from index 1
                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue

                    try {
                        val booth = Booth(
                            name = row.getCell(0)?.stringCellValue ?: "",
                            id = row.getCell(1)?.stringCellValue ?: "",
                            latitude = row.getCell(2)?.numericCellValue ?: 0.0,
                            longitude = row.getCell(3)?.numericCellValue ?: 0.0,
                            district = row.getCell(4)?.stringCellValue ?: "",
                            taluka = row.getCell(5)?.stringCellValue ?: "",
                            bloName = row.getCell(6)?.stringCellValue ?: "",
                            bloContact = row.getCell(7)?.numericCellValue.toString() ?: ""
                        )

                        // Validate booth data
                        if (booth.id.isBlank() || booth.district.isBlank()) {
                            Log.w("ExcelUpload", "Skipping row $rowIndex: Missing required fields")
                            continue
                        }

                        // Upload to Firebase
                        database
                            .child(booth.district)
                            .child(booth.name)
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





sealed class UploadState {
    data object Idle : UploadState()
    data object Uploading : UploadState()
    data class Success(val count: Int) : UploadState()
    data class Error(val message: String) : UploadState()
}