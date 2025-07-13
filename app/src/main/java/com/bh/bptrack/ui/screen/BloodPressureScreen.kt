package com.bh.bptrack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bh.bptrack.R
import com.bh.bptrack.data.entity.BloodPressureRecord
import com.bh.bptrack.ui.component.BloodPressureRecordItem
import com.bh.bptrack.ui.component.BloodPressureRecordItemCompact
import com.bh.bptrack.ui.component.AddRecordDialog
import com.bh.bptrack.ui.intent.BloodPressureIntent
import com.bh.bptrack.ui.state.BloodPressureState
import com.bh.bptrack.ui.state.ViewMode
import com.bh.bptrack.ui.theme.BPTrackAndroidTheme
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModel
import com.bh.bptrack.ui.viewmodel.BloodPressureViewModelFactory
import java.time.LocalDateTime

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

    BloodPressureScreenContent(
        state = state,
        onShowAddDialog = { viewModel.handleIntent(BloodPressureIntent.ShowAddDialog) },
        onHideAddDialog = { viewModel.handleIntent(BloodPressureIntent.HideAddDialog) },
        onEditRecord = { record -> viewModel.handleIntent(BloodPressureIntent.EditRecord(record)) },
        onDeleteRecord = { record -> viewModel.handleIntent(BloodPressureIntent.DeleteRecord(record)) },
        onSaveRecord = { viewModel.handleIntent(BloodPressureIntent.SaveRecord) },
        onSystolicChange = { viewModel.handleIntent(BloodPressureIntent.UpdateSystolic(it)) },
        onDiastolicChange = { viewModel.handleIntent(BloodPressureIntent.UpdateDiastolic(it)) },
        onHeartRateChange = { viewModel.handleIntent(BloodPressureIntent.UpdateHeartRate(it)) },
        onNotesChange = { viewModel.handleIntent(BloodPressureIntent.UpdateNotes(it)) },
        onDateTimeChange = { viewModel.handleIntent(BloodPressureIntent.UpdateDateTime(it)) },
        onClearMessage = { viewModel.handleIntent(BloodPressureIntent.ClearMessage) },
        onExportCsv = { viewModel.handleIntent(BloodPressureIntent.ExportToCsv) },
        onImportCsv = { viewModel.handleIntent(BloodPressureIntent.ImportFromCsv) },
        onProcessCsvImport = { csvContent -> viewModel.handleIntent(BloodPressureIntent.ProcessCsvImport(csvContent)) },
        onToggleViewMode = { viewMode -> viewModel.handleIntent(BloodPressureIntent.ToggleViewMode(viewMode)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureScreenContent(
    state: BloodPressureState,
    onShowAddDialog: () -> Unit,
    onHideAddDialog: () -> Unit,
    onEditRecord: (BloodPressureRecord) -> Unit,
    onDeleteRecord: (BloodPressureRecord) -> Unit,
    onSaveRecord: () -> Unit,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onHeartRateChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDateTimeChange: (LocalDateTime) -> Unit,
    onClearMessage: () -> Unit,
    onExportCsv: () -> Unit = {},
    onImportCsv: () -> Unit = {},
@Suppress("UNUSED_PARAMETER") onProcessCsvImport: (String) -> Unit = {},
    onToggleViewMode: (ViewMode) -> Unit = {}
) {
    var showMenuDropdown by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blood_pressure_records)) },
                actions = {
                    // 檢視模式切換按鈕
                    IconButton(
                        onClick = {
                            onToggleViewMode(
                                if (state.viewMode == ViewMode.DETAILED) ViewMode.COMPACT else ViewMode.DETAILED
                            )
                        }
                    ) {
                        Text(
                            text = if (state.viewMode == ViewMode.DETAILED) "📋" else "📄",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    
                    // 匯入匯出選單
                    Box {
                        IconButton(onClick = { showMenuDropdown = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "選單")
                        }
                        
                        DropdownMenu(
                            expanded = showMenuDropdown,
                            onDismissRequest = { showMenuDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.share_csv)) },
                                onClick = {
                                    showMenuDropdown = false
                                    onExportCsv()
                                },
                                leadingIcon = {
                                    Text("📤", style = MaterialTheme.typography.titleMedium)
                                },
                                enabled = !state.isExporting && state.records.isNotEmpty()
                            )
                            
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_csv)) },
                                onClick = {
                                    showMenuDropdown = false
                                    onImportCsv()
                                },
                                leadingIcon = {
                                    Text("📥", style = MaterialTheme.typography.titleMedium)
                                },
                                enabled = !state.isImporting
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddDialog
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
                    verticalArrangement = Arrangement.spacedBy(
                        if (state.viewMode == ViewMode.COMPACT) 4.dp else 8.dp
                    )
                ) {
                    itemsIndexed(state.records) { index, record ->
                        when (state.viewMode) {
                            ViewMode.COMPACT -> {
                                BloodPressureRecordItemCompact(
                                    record = record,
                                    previousRecord = if (index < state.records.size - 1) state.records[index + 1] else null,
                                    onEdit = { onEditRecord(record) },
                                    onDelete = { onDeleteRecord(record) }
                                )
                            }
                            ViewMode.DETAILED -> {
                                BloodPressureRecordItem(
                                    record = record,
                                    previousRecord = if (index < state.records.size - 1) state.records[index + 1] else null,
                                    onEdit = { onEditRecord(record) },
                                    onDelete = { onDeleteRecord(record) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 錯誤訊息
        state.error?.let { error ->
            LaunchedEffect(error) {
                // 可以在這裡顯示SnackBar或Toast
                onClearMessage()
            }
        }

        // 添加記錄對話框
        if (state.isAddDialogVisible) {
            AddRecordDialog(
                state = state,
                onSave = onSaveRecord,
                onCancel = onHideAddDialog,
                onSystolicChange = onSystolicChange,
                onDiastolicChange = onDiastolicChange,
                onHeartRateChange = onHeartRateChange,
                onNotesChange = onNotesChange,
                onDateTimeChange = onDateTimeChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureScreenEmptyPreview() {
    BPTrackAndroidTheme {
        BloodPressureScreenContent(
            state = BloodPressureState(
                records = emptyList(),
                isLoading = false
            ),
            onShowAddDialog = {},
            onHideAddDialog = {},
            onEditRecord = {},
            onDeleteRecord = {},
            onSaveRecord = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {},
            onClearMessage = {},
            onExportCsv = {},
            onImportCsv = {},
            onProcessCsvImport = {},
            onToggleViewMode = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureScreenLoadingPreview() {
    BPTrackAndroidTheme {
        BloodPressureScreenContent(
            state = BloodPressureState(
                records = emptyList(),
                isLoading = true
            ),
            onShowAddDialog = {},
            onHideAddDialog = {},
            onEditRecord = {},
            onDeleteRecord = {},
            onSaveRecord = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {},
            onClearMessage = {},
            onExportCsv = {},
            onImportCsv = {},
            onProcessCsvImport = {},
            onToggleViewMode = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureScreenWithRecordsPreview() {
    BPTrackAndroidTheme {
        BloodPressureScreenContent(
            state = BloodPressureState(
                records = listOf(
                    BloodPressureRecord(
                        id = 3,
                        systolic = 115,
                        diastolic = 75,
                        heartRate = 70,
                        dateTime = LocalDateTime.of(2024, 1, 16, 9, 30),
                        notes = "早晨測量，感覺良好"
                    ),
                    BloodPressureRecord(
                        id = 2,
                        systolic = 135,
                        diastolic = 85,
                        heartRate = 80,
                        dateTime = LocalDateTime.of(2024, 1, 15, 18, 45),
                        notes = "下午測量"
                    ),
                    BloodPressureRecord(
                        id = 1,
                        systolic = 120,
                        diastolic = 80,
                        heartRate = 75,
                        dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                        notes = "首次測量"
                    )
                ),
                isLoading = false
            ),
            onShowAddDialog = {},
            onHideAddDialog = {},
            onEditRecord = {},
            onDeleteRecord = {},
            onSaveRecord = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {},
            onClearMessage = {},
            onExportCsv = {},
            onImportCsv = {},
            onProcessCsvImport = {},
            onToggleViewMode = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureScreenWithDialogPreview() {
    BPTrackAndroidTheme {
        BloodPressureScreenContent(
            state = BloodPressureState(
                records = listOf(
                    BloodPressureRecord(
                        id = 1,
                        systolic = 120,
                        diastolic = 80,
                        heartRate = 75,
                        dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                        notes = "早晨測量"
                    )
                ),
                isLoading = false,
                isAddDialogVisible = true,
                systolicInput = "130",
                diastolicInput = "85",
                heartRateInput = "80",
                notesInput = "晚上測量",
                selectedDateTime = LocalDateTime.now()
            ),
            onShowAddDialog = {},
            onHideAddDialog = {},
            onEditRecord = {},
            onDeleteRecord = {},
            onSaveRecord = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {},
            onClearMessage = {},
            onExportCsv = {},
            onImportCsv = {},
            onProcessCsvImport = {},
            onToggleViewMode = {}
        )
    }
} 