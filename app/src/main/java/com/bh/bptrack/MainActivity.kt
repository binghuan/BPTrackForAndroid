package com.bh.bptrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bh.bptrack.data.database.BloodPressureDatabase
import com.bh.bptrack.data.repository.BloodPressureRepository
import com.bh.bptrack.ui.intent.BloodPressureIntent
import com.bh.bptrack.ui.screen.BloodPressureScreen
import com.bh.bptrack.ui.theme.BPTrackAndroidTheme
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModel
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModelFactory
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {
    
    // 文件選擇器
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedFileUri ->
            try {
                val inputStream = contentResolver.openInputStream(selectedFileUri)
                val csvContent = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                viewModel?.handleIntent(BloodPressureIntent.ProcessCsvImport(csvContent))
            } catch (e: IOException) {
                Toast.makeText(this, "讀取檔案失敗: ${e.message}", Toast.LENGTH_LONG).show()
                viewModel?.handleIntent(BloodPressureIntent.ClearMessage)
            }
        } ?: run {
            // 用戶取消選擇
            viewModel?.handleIntent(BloodPressureIntent.ClearMessage)
        }
    }
    
    private var viewModel: BloodPressureViewModel? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化數據庫和Repository
        val database = BloodPressureDatabase.getDatabase(this)
        val repository = BloodPressureRepository(database.bloodPressureDao())
        val viewModelFactory = BloodPressureViewModelFactory(repository)
        
        setContent {
            BPTrackAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: BloodPressureViewModel = viewModel(factory = viewModelFactory)
                    
                    // 儲存 ViewModel 引用
                    LaunchedEffect(vm) {
                        viewModel = vm
                    }
                    
                    val state by vm.state.collectAsState()
                    
                    // 監聽匯出狀態，當有 CSV 資料時分享
                    LaunchedEffect(state.csvExportData) {
                        state.csvExportData?.let { csvData ->
                            shareCsvFile(csvData)
                            vm.handleIntent(BloodPressureIntent.ClearMessage)
                        }
                    }
                    
                    // 監聽匯入狀態，當需要選擇檔案時啟動文件選擇器
                    LaunchedEffect(state.isImporting) {
                        if (state.isImporting && state.importProgress == "請選擇 CSV 檔案...") {
                            filePickerLauncher.launch("text/*")
                        }
                    }
                    
                    BloodPressureScreen(
                        viewModel = vm
                    )
                }
            }
        }
    }
    
    private fun shareCsvFile(csvContent: String) {
        try {
            // 創建臨時檔案
            val file = File(cacheDir, "blood_pressure_records.csv")
            file.writeText(csvContent)
            
            // 創建 URI
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            // 創建分享 Intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "血壓記錄")
                putExtra(Intent.EXTRA_TEXT, "血壓記錄 CSV 檔案")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            
            startActivity(Intent.createChooser(shareIntent, "分享血壓記錄"))
            
        } catch (e: Exception) {
            Toast.makeText(this, "分享失敗: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 