package com.example.bptrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bptrack.R
import com.example.bptrack.ui.component.BloodPressureRecordItem
import com.example.bptrack.ui.component.AddRecordDialog
import com.example.bptrack.ui.intent.BloodPressureIntent
import com.example.bptrack.ui.viewmodel.BloodPressureViewModel
import com.example.bptrack.ui.viewmodel.BloodPressureViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureScreen(
    viewModel: BloodPressureViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.message) {
        state.message?.let {
            // 顯示消息後清除
            viewModel.handleIntent(BloodPressureIntent.ClearMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blood_pressure_records)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(BloodPressureIntent.ShowAddDialog) }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_record))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_records),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.records) { record ->
                        BloodPressureRecordItem(
                            record = record,
                            onEdit = { viewModel.handleIntent(BloodPressureIntent.EditRecord(record)) },
                            onDelete = { viewModel.handleIntent(BloodPressureIntent.DeleteRecord(record)) }
                        )
                    }
                }
            }
        }

        // 錯誤訊息
        state.error?.let { error ->
            LaunchedEffect(error) {
                // 可以在這裡顯示SnackBar或Toast
                viewModel.handleIntent(BloodPressureIntent.ClearMessage)
            }
        }

        // 添加記錄對話框
        if (state.isAddDialogVisible) {
            AddRecordDialog(
                state = state,
                onSave = { viewModel.handleIntent(BloodPressureIntent.SaveRecord) },
                onCancel = { viewModel.handleIntent(BloodPressureIntent.HideAddDialog) },
                onSystolicChange = { viewModel.handleIntent(BloodPressureIntent.UpdateSystolic(it)) },
                onDiastolicChange = { viewModel.handleIntent(BloodPressureIntent.UpdateDiastolic(it)) },
                onHeartRateChange = { viewModel.handleIntent(BloodPressureIntent.UpdateHeartRate(it)) },
                onNotesChange = { viewModel.handleIntent(BloodPressureIntent.UpdateNotes(it)) },
                onDateTimeChange = { viewModel.handleIntent(BloodPressureIntent.UpdateDateTime(it)) }
            )
        }
    }
} 