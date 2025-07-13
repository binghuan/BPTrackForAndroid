package com.example.bptrack.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bptrack.R
import com.example.bptrack.data.entity.BloodPressureRecord
import com.example.bptrack.ui.state.BloodPressureState
import com.example.bptrack.ui.theme.BPTrackAndroidTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 血壓分類枚舉
enum class BloodPressureCategory(
    val nameRes: Int,
    val color: Color,
    val descriptionRes: Int
) {
    NORMAL(R.string.bp_category_normal, Color(0xFF4CAF50), R.string.bp_category_normal_desc),
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

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                    OutlinedTextField(
                        value = state.selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.date)) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { /* 日期選擇器 */ }) {
                                Icon(Icons.Default.DateRange, contentDescription = null)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = state.selectedDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.time)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 備註
                OutlinedTextField(
                    value = state.notesInput,
                    onValueChange = onNotesChange,
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // 按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
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