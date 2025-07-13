package com.example.bptrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bptrack.data.entity.BloodPressureRecord
import com.example.bptrack.data.repository.BloodPressureRepository
import com.example.bptrack.ui.intent.BloodPressureIntent
import com.example.bptrack.ui.state.BloodPressureState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class BloodPressureViewModel(
    private val repository: BloodPressureRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BloodPressureState())
    val state: StateFlow<BloodPressureState> = _state.asStateFlow()

    init {
        handleIntent(BloodPressureIntent.LoadRecords)
    }

    fun handleIntent(intent: BloodPressureIntent) {
        when (intent) {
            is BloodPressureIntent.LoadRecords -> loadRecords()
            is BloodPressureIntent.ShowAddDialog -> showAddDialog()
            is BloodPressureIntent.HideAddDialog -> hideAddDialog()
            is BloodPressureIntent.ShowDatePicker -> showDatePicker()
            is BloodPressureIntent.HideDatePicker -> hideDatePicker()
            is BloodPressureIntent.ShowTimePicker -> showTimePicker()
            is BloodPressureIntent.HideTimePicker -> hideTimePicker()
            is BloodPressureIntent.SaveRecord -> saveRecord()
            is BloodPressureIntent.ClearMessage -> clearMessage()
            is BloodPressureIntent.EditRecord -> editRecord(intent.record)
            is BloodPressureIntent.DeleteRecord -> deleteRecord(intent.record)
            is BloodPressureIntent.UpdateSystolic -> updateSystolic(intent.value)
            is BloodPressureIntent.UpdateDiastolic -> updateDiastolic(intent.value)
            is BloodPressureIntent.UpdateHeartRate -> updateHeartRate(intent.value)
            is BloodPressureIntent.UpdateNotes -> updateNotes(intent.value)
            is BloodPressureIntent.UpdateDateTime -> updateDateTime(intent.dateTime)
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
                _state.value = _state.value.copy(message = "記錄已刪除")
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
            _state.value = currentState.copy(error = "請輸入有效的血壓值")
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
                        message = "記錄已更新"
                    )
                } else {
                    repository.insertRecord(record)
                    _state.value = currentState.copy(
                        isAddDialogVisible = false,
                        message = "記錄已儲存"
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
        _state.value = _state.value.copy(message = null, error = null)
    }
} 