package com.bh.bptrack.ui.intent

import com.bh.bptrack.data.entity.BloodPressureRecord
import java.time.LocalDateTime

sealed class BloodPressureIntent {
    object LoadRecords : BloodPressureIntent()
    object ShowAddDialog : BloodPressureIntent()
    object HideAddDialog : BloodPressureIntent()
    object ShowDatePicker : BloodPressureIntent()
    object HideDatePicker : BloodPressureIntent()
    object ShowTimePicker : BloodPressureIntent()
    object HideTimePicker : BloodPressureIntent()
    object SaveRecord : BloodPressureIntent()
    object ClearMessage : BloodPressureIntent()
    
    data class EditRecord(val record: BloodPressureRecord) : BloodPressureIntent()
    data class DeleteRecord(val record: BloodPressureRecord) : BloodPressureIntent()
    data class UpdateSystolic(val value: String) : BloodPressureIntent()
    data class UpdateDiastolic(val value: String) : BloodPressureIntent()
    data class UpdateHeartRate(val value: String) : BloodPressureIntent()
    data class UpdateNotes(val value: String) : BloodPressureIntent()
    data class UpdateDateTime(val dateTime: LocalDateTime) : BloodPressureIntent()
} 