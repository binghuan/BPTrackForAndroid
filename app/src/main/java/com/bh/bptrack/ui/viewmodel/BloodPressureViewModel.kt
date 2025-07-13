package com.bh.bptrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bh.bptrack.data.entity.BloodPressureRecord
import com.bh.bptrack.data.repository.BloodPressureRepository
import com.bh.bptrack.ui.intent.BloodPressureIntent
import com.bh.bptrack.ui.state.BloodPressureState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class BloodPressureViewModel(
    private val repository: BloodPressureRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BloodPressureState())
    val state: StateFlow<BloodPressureState> = _state.asStateFlow()

    init {
        loadRecords()
    }

    fun handleIntent(intent: BloodPressureIntent) {
        when (intent) {
            is BloodPressureIntent.ShowAddDialog -> showAddDialog()
            is BloodPressureIntent.HideAddDialog -> hideAddDialog()
            is BloodPressureIntent.ShowDatePicker -> showDatePicker()
            is BloodPressureIntent.HideDatePicker -> hideDatePicker()
            is BloodPressureIntent.ShowTimePicker -> showTimePicker()
            is BloodPressureIntent.HideTimePicker -> hideTimePicker()
            is BloodPressureIntent.SaveRecord -> saveRecord()
            is BloodPressureIntent.ClearMessage -> clearMessage()
            is BloodPressureIntent.ExportToCsv -> exportToCsv()
            is BloodPressureIntent.ImportFromCsv -> importFromCsv()
            is BloodPressureIntent.ProcessCsvImport -> processCsvImport(intent.csvContent)
            is BloodPressureIntent.EditRecord -> editRecord(intent.record)
            is BloodPressureIntent.DeleteRecord -> deleteRecord(intent.record)
            is BloodPressureIntent.UpdateSystolic -> updateSystolic(intent.value)
            is BloodPressureIntent.UpdateDiastolic -> updateDiastolic(intent.value)
            is BloodPressureIntent.UpdateHeartRate -> updateHeartRate(intent.value)
            is BloodPressureIntent.UpdateNotes -> updateNotes(intent.value)
            is BloodPressureIntent.UpdateDateTime -> updateDateTime(intent.dateTime)
            is BloodPressureIntent.ToggleViewMode -> toggleViewMode(intent.viewMode)
        }
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getAllRecords().collect { records ->
                    _state.value = _state.value.copy(
                        records = records,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun showAddDialog() {
        _state.value = _state.value.copy(
            isAddDialogVisible = true,
            editingRecord = null,
            systolicInput = "",
            diastolicInput = "",
            heartRateInput = "",
            notesInput = "",
            selectedDateTime = LocalDateTime.now()
        )
    }

    private fun hideAddDialog() {
        _state.value = _state.value.copy(
            isAddDialogVisible = false,
            editingRecord = null,
            systolicInput = "",
            diastolicInput = "",
            heartRateInput = "",
            notesInput = ""
        )
    }

    private fun showDatePicker() {
        _state.value = _state.value.copy(isDatePickerVisible = true)
    }

    private fun hideDatePicker() {
        _state.value = _state.value.copy(isDatePickerVisible = false)
    }

    private fun showTimePicker() {
        _state.value = _state.value.copy(isTimePickerVisible = true)
    }

    private fun hideTimePicker() {
        _state.value = _state.value.copy(isTimePickerVisible = false)
    }

    private fun editRecord(record: BloodPressureRecord) {
        _state.value = _state.value.copy(
            isAddDialogVisible = true,
            editingRecord = record,
            systolicInput = record.systolic.toString(),
            diastolicInput = record.diastolic.toString(),
            heartRateInput = record.heartRate?.toString() ?: "",
            notesInput = record.notes ?: "",
            selectedDateTime = record.dateTime
        )
    }

    private fun deleteRecord(record: BloodPressureRecord) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(record)
                _state.value = _state.value.copy(message = "Record deleted")
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun saveRecord() {
        val currentState = _state.value
        val systolic = currentState.systolicInput.toIntOrNull()
        val diastolic = currentState.diastolicInput.toIntOrNull()
        val heartRate = currentState.heartRateInput.toIntOrNull()

        if (systolic == null || diastolic == null) {
            _state.value = currentState.copy(error = "Please enter valid blood pressure values")
            return
        }

        viewModelScope.launch {
            try {
                val record = BloodPressureRecord(
                    id = currentState.editingRecord?.id ?: 0,
                    systolic = systolic,
                    diastolic = diastolic,
                    heartRate = heartRate,
                    dateTime = currentState.selectedDateTime,
                    notes = currentState.notesInput.ifBlank { null }
                )

                if (currentState.editingRecord != null) {
                    repository.updateRecord(record)
                    _state.value = currentState.copy(
                        isAddDialogVisible = false,
                        message = "Record updated"
                    )
                } else {
                    repository.insertRecord(record)
                    _state.value = currentState.copy(
                        isAddDialogVisible = false,
                        message = "Record saved"
                    )
                }
            } catch (e: Exception) {
                _state.value = currentState.copy(error = e.message)
            }
        }
    }

    private fun updateSystolic(value: String) {
        _state.value = _state.value.copy(systolicInput = value)
    }

    private fun updateDiastolic(value: String) {
        _state.value = _state.value.copy(diastolicInput = value)
    }

    private fun updateHeartRate(value: String) {
        _state.value = _state.value.copy(heartRateInput = value)
    }

    private fun updateNotes(value: String) {
        _state.value = _state.value.copy(notesInput = value)
    }

    private fun updateDateTime(dateTime: LocalDateTime) {
        _state.value = _state.value.copy(selectedDateTime = dateTime)
    }

    private fun clearMessage() {
        _state.value = _state.value.copy(message = null, error = null, csvExportData = null, importProgress = null)
    }
    
    // CSV 匯出功能
    private fun exportToCsv() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true, error = null)
            
            try {
                val result = repository.exportToCsv()
                if (result.isSuccess) {
                    _state.value = _state.value.copy(
                        isExporting = false,
                        csvExportData = result.getOrNull(),
                        message = "資料匯出成功"
                    )
                } else {
                    _state.value = _state.value.copy(
                        isExporting = false,
                        error = result.exceptionOrNull()?.message ?: "匯出失敗"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isExporting = false,
                    error = "匯出過程中發生錯誤: ${e.message}"
                )
            }
        }
    }
    
    // 開始 CSV 匯入流程 (觸發文件選擇)
    private fun importFromCsv() {
        _state.value = _state.value.copy(
            isImporting = true,
            error = null,
            importProgress = "請選擇 CSV 檔案..."
        )
    }
    
    // 處理 CSV 匯入內容
    private fun processCsvImport(csvContent: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isImporting = true,
                importProgress = "正在處理 CSV 資料..."
            )
            
            try {
                val result = repository.importFromCsv(csvContent)
                if (result.isSuccess) {
                    val importedCount = result.getOrNull() ?: 0
                    _state.value = _state.value.copy(
                        isImporting = false,
                        importProgress = null,
                        message = "成功匯入 $importedCount 筆記錄"
                    )
                    // Room Flow 會自動更新記錄列表
                } else {
                    _state.value = _state.value.copy(
                        isImporting = false,
                        importProgress = null,
                        error = result.exceptionOrNull()?.message ?: "匯入失敗"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isImporting = false,
                    importProgress = null,
                    error = "匯入過程中發生錯誤: ${e.message}"
                )
            }
        }
    }
    
    private fun toggleViewMode(viewMode: com.bh.bptrack.ui.state.ViewMode) {
        _state.value = _state.value.copy(viewMode = viewMode)
    }
} 