package com.bh.bptrack.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bh.bptrack.R
import com.bh.bptrack.ui.state.BloodPressureState
import com.bh.bptrack.ui.theme.BPTrackAndroidTheme
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 血壓分類枚舉
enum class BloodPressureCategory(
    val nameRes: Int,
    val color: Color,
    val descriptionRes: Int
) {
    NORMAL(R.string.bp_category_normal, Color(0xFF2E7D32), R.string.bp_category_normal_desc),
    ELEVATED(R.string.bp_category_elevated, Color(0xFFFF9800), R.string.bp_category_elevated_desc),
    HIGH_STAGE_1(R.string.bp_category_high_stage_1, Color(0xFFFF5722), R.string.bp_category_high_stage_1_desc),
    HIGH_STAGE_2(R.string.bp_category_high_stage_2, Color(0xFFD32F2F), R.string.bp_category_high_stage_2_desc),
    HYPERTENSIVE_CRISIS(R.string.bp_category_hypertensive_crisis, Color(0xFF880E4F), R.string.bp_category_hypertensive_crisis_desc),
    INVALID(R.string.bp_category_invalid, Color.Gray, R.string.please_enter_valid_bp_values)
}

// 計算血壓分類
fun calculateBloodPressureCategory(systolic: Int?, diastolic: Int?): BloodPressureCategory {
    if (systolic == null || diastolic == null || systolic <= 0 || diastolic <= 0) {
        return BloodPressureCategory.INVALID
    }
    
    return when {
        systolic >= 180 || diastolic >= 120 -> BloodPressureCategory.HYPERTENSIVE_CRISIS
        systolic >= 140 || diastolic >= 90 -> BloodPressureCategory.HIGH_STAGE_2
        systolic >= 130 || diastolic >= 80 -> BloodPressureCategory.HIGH_STAGE_1
        systolic >= 120 && diastolic < 80 -> BloodPressureCategory.ELEVATED
        systolic < 120 && diastolic < 80 -> BloodPressureCategory.NORMAL
        else -> BloodPressureCategory.HIGH_STAGE_1
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordDialog(
    state: BloodPressureState,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onHeartRateChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDateTimeChange: (LocalDateTime) -> Unit
) {
    // 計算當前血壓分類
    val systolicValue = state.systolicInput.toIntOrNull()
    val diastolicValue = state.diastolicInput.toIntOrNull()
    val currentCategory = calculateBloodPressureCategory(systolicValue, diastolicValue)
    
    // 判斷輸入是否有效
    val isSystolicValid = systolicValue != null && systolicValue > 0
    val isDiastolicValid = diastolicValue != null && diastolicValue > 0
    val showCategory = isSystolicValid && isDiastolicValid

    // DatePicker 和 TimePicker 狀態
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // DatePicker 狀態
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.selectedDateTime
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )
    
    // TimePicker 狀態
    val timePickerState = rememberTimePickerState(
        initialHour = state.selectedDateTime.hour,
        initialMinute = state.selectedDateTime.minute
    )

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            val newDateTime = LocalDateTime.of(
                                selectedDate,
                                state.selectedDateTime.toLocalTime()
                            )
                            onDateTimeChange(newDateTime)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker Dialog
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_time),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    TimePicker(state = timePickerState)
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                val selectedTime = LocalTime.of(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )
                                val newDateTime = LocalDateTime.of(
                                    state.selectedDateTime.toLocalDate(),
                                    selectedTime
                                )
                                onDateTimeChange(newDateTime)
                                showTimePicker = false
                            }
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 標題
                Text(
                    text = if (state.editingRecord != null) stringResource(R.string.edit_record) else stringResource(R.string.add_record),
                    style = MaterialTheme.typography.headlineSmall
                )

                // 收縮壓
                OutlinedTextField(
                    value = state.systolicInput,
                    onValueChange = onSystolicChange,
                    label = { Text(stringResource(R.string.systolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("mmHg") },
                    isError = state.systolicInput.isNotEmpty() && !isSystolicValid,
                    colors = if (showCategory) {
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentCategory.color,
                            unfocusedBorderColor = currentCategory.color.copy(alpha = 0.6f)
                        )
                    } else OutlinedTextFieldDefaults.colors()
                )

                // 舒張壓
                OutlinedTextField(
                    value = state.diastolicInput,
                    onValueChange = onDiastolicChange,
                    label = { Text(stringResource(R.string.diastolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("mmHg") },
                    isError = state.diastolicInput.isNotEmpty() && !isDiastolicValid,
                    colors = if (showCategory) {
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentCategory.color,
                            unfocusedBorderColor = currentCategory.color.copy(alpha = 0.6f)
                        )
                    } else OutlinedTextFieldDefaults.colors()
                )

                // 血壓分類提示
                if (showCategory && currentCategory != BloodPressureCategory.INVALID) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = currentCategory.color.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        currentCategory.color,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = stringResource(currentCategory.nameRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = currentCategory.color
                                )
                                Text(
                                    text = stringResource(currentCategory.descriptionRes),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 心率
                OutlinedTextField(
                    value = state.heartRateInput,
                    onValueChange = onHeartRateChange,
                    label = { Text(stringResource(R.string.heart_rate)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("bpm") }
                )

                // 日期時間選擇器
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 日期選擇器
                    val dateInteractionSource = remember { MutableInteractionSource() }
                    
                    LaunchedEffect(dateInteractionSource) {
                        dateInteractionSource.interactions.collect { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> {
                                    showDatePicker = true
                                }
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = state.selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.date)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        interactionSource = dateInteractionSource
                    )

                    // 時間選擇器
                    val timeInteractionSource = remember { MutableInteractionSource() }
                    
                    LaunchedEffect(timeInteractionSource) {
                        timeInteractionSource.interactions.collect { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> {
                                    showTimePicker = true
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.selectedDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.time)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        interactionSource = timeInteractionSource
                    )
                }

                // 備註
                OutlinedTextField(
                    value = state.notesInput,
                    onValueChange = onNotesChange,
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 1,
                    maxLines = 5
                )

                // 按鈕
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancel) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        enabled = isSystolicValid && isDiastolicValid
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddRecordDialogPreview() {
    BPTrackAndroidTheme {
        AddRecordDialog(
            state = BloodPressureState(
                isAddDialogVisible = true,
                systolicInput = "120",
                diastolicInput = "80",
                heartRateInput = "75",
                notesInput = "早晨測量",
                selectedDateTime = LocalDateTime.now()
            ),
            onSave = {},
            onCancel = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {}
        )
    }
}

@Preview
@Composable
fun AddRecordDialogHighBPPreview() {
    BPTrackAndroidTheme {
        AddRecordDialog(
            state = BloodPressureState(
                isAddDialogVisible = true,
                systolicInput = "150",
                diastolicInput = "95",
                heartRateInput = "85",
                notesInput = "運動後測量",
                selectedDateTime = LocalDateTime.now()
            ),
            onSave = {},
            onCancel = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {}
        )
    }
}

@Preview
@Composable
fun AddRecordDialogEmptyPreview() {
    BPTrackAndroidTheme {
        AddRecordDialog(
            state = BloodPressureState(
                isAddDialogVisible = true,
                systolicInput = "",
                diastolicInput = "",
                heartRateInput = "",
                notesInput = "",
                selectedDateTime = LocalDateTime.now()
            ),
            onSave = {},
            onCancel = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onHeartRateChange = {},
            onNotesChange = {},
            onDateTimeChange = {}
        )
    }
} 