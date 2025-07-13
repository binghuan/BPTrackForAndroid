package com.example.bptrack.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bptrack.R
import com.example.bptrack.data.entity.BloodPressureRecord
import com.example.bptrack.ui.theme.BPTrackAndroidTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureRecordItem(
    record: BloodPressureRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 血壓值
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${record.systolic}/${record.diastolic}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.mmhg),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 心率
                    record.heartRate?.let { heartRate ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(R.string.heart_rate)}: $heartRate",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.bpm),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 日期時間
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = record.dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 備註
                    record.notes?.let { notes ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 操作按鈕
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }
    }

    // 刪除確認對話框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text("確定要刪除此記錄嗎？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureRecordItemPreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 1,
                systolic = 120,
                diastolic = 80,
                heartRate = 75,
                dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                notes = "早晨測量，感覺良好"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureRecordItemNoHeartRatePreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 2,
                systolic = 135,
                diastolic = 85,
                heartRate = null,
                dateTime = LocalDateTime.of(2024, 1, 15, 18, 45),
                notes = null
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureRecordItemHighBPPreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 3,
                systolic = 150,
                diastolic = 95,
                heartRate = 88,
                dateTime = LocalDateTime.of(2024, 1, 15, 21, 15),
                notes = "運動後測量"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 