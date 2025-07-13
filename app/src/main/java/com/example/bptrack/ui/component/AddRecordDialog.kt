package com.example.bptrack.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bptrack.R
import com.example.bptrack.ui.state.BloodPressureState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 標題
                Text(
                    text = if (state.editingRecord != null) "編輯記錄" else stringResource(R.string.add_record),
                    style = MaterialTheme.typography.headlineSmall
                )

                // 收縮壓
                OutlinedTextField(
                    value = state.systolicInput,
                    onValueChange = onSystolicChange,
                    label = { Text(stringResource(R.string.systolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("mmHg") }
                )

                // 舒張壓
                OutlinedTextField(
                    value = state.diastolicInput,
                    onValueChange = onDiastolicChange,
                    label = { Text(stringResource(R.string.diastolic)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Text("mmHg") }
                )

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
                    Button(onClick = onSave) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
} 