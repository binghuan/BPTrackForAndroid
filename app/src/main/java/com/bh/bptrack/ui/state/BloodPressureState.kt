package com.bh.bptrack.ui.state

import com.bh.bptrack.data.entity.BloodPressureRecord

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
    val message: String? = null,
    
    // CSV 匯入匯出狀態
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val csvExportData: String? = null,
    val importProgress: String? = null
) 