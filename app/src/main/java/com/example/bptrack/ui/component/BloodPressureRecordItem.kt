package com.example.bptrack.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bptrack.R
import com.example.bptrack.data.entity.BloodPressureRecord
import com.example.bptrack.ui.theme.BPTrackAndroidTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 血壓分類枚舉
enum class BPCategory(
    val displayName: String,
    val color: Color,
    val description: String
) {
    NORMAL("正常", Color(0xFF4CAF50), "收縮壓 < 120 且 舒張壓 < 80"),
    ELEVATED("血壓升高", Color(0xFFFF9800), "收縮壓 120-129 且 舒張壓 < 80"),
    HIGH_STAGE_1("高血壓1期", Color(0xFFFF5722), "收縮壓 130-139 或 舒張壓 80-89"),
    HIGH_STAGE_2("高血壓2期", Color(0xFFD32F2F), "收縮壓 140-179 或 舒張壓 90-119"),
    HYPERTENSIVE_CRISIS("高血壓危象", Color(0xFF880E4F), "收縮壓 ≥ 180 或 舒張壓 ≥ 120")
}

// 計算血壓分類
fun calculateBPCategory(systolic: Int, diastolic: Int): BPCategory {
    return when {
        systolic >= 180 || diastolic >= 120 -> BPCategory.HYPERTENSIVE_CRISIS
        systolic >= 140 || diastolic >= 90 -> BPCategory.HIGH_STAGE_2
        systolic >= 130 || diastolic >= 80 -> BPCategory.HIGH_STAGE_1
        systolic >= 120 && diastolic < 80 -> BPCategory.ELEVATED
        systolic < 120 && diastolic < 80 -> BPCategory.NORMAL
        else -> BPCategory.HIGH_STAGE_1
    }
}

// 血壓趨勢枚舉
enum class BloodPressureTrend {
    INCREASED,    // 上升
    DECREASED,    // 下降
    STABLE,       // 穩定
    FIRST_RECORD  // 首次記錄
}

// 計算血壓趋势
fun calculateBloodPressureTrend(
    current: BloodPressureRecord,
    previous: BloodPressureRecord?
): BloodPressureTrend {
    if (previous == null) return BloodPressureTrend.FIRST_RECORD
    
    val currentAvg = (current.systolic + current.diastolic) / 2.0
    val previousAvg = (previous.systolic + previous.diastolic) / 2.0
    
    return when {
        currentAvg > previousAvg + 2 -> BloodPressureTrend.INCREASED
        currentAvg < previousAvg - 2 -> BloodPressureTrend.DECREASED
        else -> BloodPressureTrend.STABLE
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureRecordItem(
    record: BloodPressureRecord,
    previousRecord: BloodPressureRecord? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val trend = calculateBloodPressureTrend(record, previousRecord)
    val bpCategory = calculateBPCategory(record.systolic, record.diastolic)

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
                    // 血壓值和趨勢
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
                        
                        // 趨勢指示器
                        when (trend) {
                            BloodPressureTrend.INCREASED -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp,
                                        contentDescription = "上升",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "上升",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Red
                                    )
                                }
                            }
                            BloodPressureTrend.DECREASED -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "下降",
                                        tint = Color.Green,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "下降",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Green
                                    )
                                }
                            }
                            BloodPressureTrend.STABLE -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "穩定",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            BloodPressureTrend.FIRST_RECORD -> {
                                // 首次記錄不顯示趨勢
                            }
                        }
                    }
                    
                    // 血壓分類標籤
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    bpCategory.color,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bpCategory.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = bpCategory.color,
                            fontWeight = FontWeight.Medium
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
fun BloodPressureRecordItemWithTrendPreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 2,
                systolic = 135,
                diastolic = 85,
                heartRate = 80,
                dateTime = LocalDateTime.of(2024, 1, 15, 18, 45),
                notes = "下午測量"
            ),
            previousRecord = BloodPressureRecord(
                id = 1,
                systolic = 120,
                diastolic = 80,
                heartRate = 75,
                dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                notes = "早晨測量"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureRecordItemDecreasedTrendPreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 2,
                systolic = 115,
                diastolic = 75,
                heartRate = 70,
                dateTime = LocalDateTime.of(2024, 1, 15, 18, 45),
                notes = "下午測量，感覺不錯"
            ),
            previousRecord = BloodPressureRecord(
                id = 1,
                systolic = 130,
                diastolic = 85,
                heartRate = 80,
                dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                notes = "早晨測量"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BloodPressureRecordItemStableTrendPreview() {
    BPTrackAndroidTheme {
        BloodPressureRecordItem(
            record = BloodPressureRecord(
                id = 2,
                systolic = 121,
                diastolic = 81,
                heartRate = 76,
                dateTime = LocalDateTime.of(2024, 1, 15, 18, 45),
                notes = "下午測量"
            ),
            previousRecord = BloodPressureRecord(
                id = 1,
                systolic = 120,
                diastolic = 80,
                heartRate = 75,
                dateTime = LocalDateTime.of(2024, 1, 15, 9, 30),
                notes = "早晨測量"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
} 