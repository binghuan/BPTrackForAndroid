package com.bh.bptrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bh.bptrack.data.database.BloodPressureDatabase
import com.bh.bptrack.data.repository.BloodPressureRepository
import com.bh.bptrack.ui.screen.BloodPressureScreen
import com.bh.bptrack.ui.theme.BPTrackAndroidTheme
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModel
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModelFactory

class MainActivity : ComponentActivity() {
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
                    BloodPressureScreen(
                        viewModel = viewModel(factory = viewModelFactory)
                    )
                }
            }
        }
    }
} 