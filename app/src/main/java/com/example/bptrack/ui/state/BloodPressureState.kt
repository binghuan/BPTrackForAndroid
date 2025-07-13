package com.example.bptrack.ui.state

import com.example.bptrack.data.entity.BloodPressureRecord

data class BloodPressureState(
    val records: List<BloodPressureRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddDialogVisible: Boolean = false,
    val editingRecord: BloodPressureRecord? = null,
    val systolicInput: String = "",
    val diastolicInput: String = "",
    val heartRateInput: String = "",
    val notesInput: String = "",
    val selectedDateTime: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    val isDatePickerVisible: Boolean = false,
    val isTimePickerVisible: Boolean = false,
    val message: String? = null
) 