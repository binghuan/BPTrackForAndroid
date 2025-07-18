package com.bh.bptrack.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bh.bptrack.R
import com.bh.bptrack.data.entity.BloodPressureRecord
import com.bh.bptrack.ui.theme.BPTrackAndroidTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 血壓分類枚舉
enum class BPCategory(
    val nameRes: Int,
    val color: Color,
    val descriptionRes: Int
) {
    NORMAL(R.string.bp_category_normal, Color(0xFF2E7D32), R.string.bp_category_normal_desc),
    ELEVATED(R.string.bp_category_elevated, Color(0xFFFF9800), R.string.bp_category_elevated_desc),
    HIGH_STAGE_1(R.string.bp_category_high_stage_1, Color(0xFFFF5722), R.string.bp_category_high_stage_1_desc),
    HIGH_STAGE_2(R.string.bp_category_high_stage_2, Color(0xFFD32F2F), R.string.bp_category_high_stage_2_desc),
    HYPERTENSIVE_CRISIS(R.string.bp_category_hypertensive_crisis, Color(0xFF880E4F), R.string.bp_category_hypertensive_crisis_desc)
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

// 根據主題模式取得血壓分類的文字顏色
@Composable
fun getBPCategoryTextColor(
    category: BPCategory,
    isDarkTheme: Boolean = isSystemInDarkTheme()
): Color {
    return when (category) {
        BPCategory.NORMAL -> if (isDarkTheme) Color(0xFF66BB6A) else Color(0xFF1B5E20)
        BPCategory.ELEVATED -> if (isDarkTheme) Color(0xFFFFB74D) else Color(0xFFE65100)
        BPCategory.HIGH_STAGE_1 -> if (isDarkTheme) Color(0xFFFF8A65) else Color(0xFFBF360C)
        BPCategory.HIGH_STAGE_2 -> if (isDarkTheme) Color(0xFFEF5350) else Color(0xFF8B0000)
        BPCategory.HYPERTENSIVE_CRISIS -> if (isDarkTheme) Color(0xFFAD1457) else Color(0xFF4A0E4E)
    }
}

// 簡潔的血壓記錄項組件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureRecordItemCompact(
    record: BloodPressureRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val bpCategory = calculateBPCategory(record.systolic, record.diastolic)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：日期 (wrap content) - 顯示年月日
            Text(
                text = record.dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // 中間：血壓值 (weight = 1)
            Text(
                text = "${record.systolic}/${record.diastolic}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = getBPCategoryTextColor(bpCategory),
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            
            // 右側：脈搏和選單
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = record.heartRate?.toString() ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 備註指示器
                if (!record.notes.isNullOrBlank()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "有備註",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                // 選單按鈕
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "選項",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("編輯") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("刪除") },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
        
        // 如果有備註，顯示在下方
        if (!record.notes.isNullOrBlank()) {
            Divider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 0.5.dp
            )
            Text(
                text = record.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp, top = 4.dp)
            )
        }
    }
    
    // 刪除確認對話框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("刪除記錄") },
            text = { Text("確定要刪除這筆血壓記錄嗎？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("刪除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
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
                                        contentDescription = stringResource(R.string.trend_increased),
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.trend_increased),
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
                                        contentDescription = stringResource(R.string.trend_decreased),
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.trend_decreased),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                            BloodPressureTrend.STABLE -> {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.trend_stable),
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
                            text = stringResource(bpCategory.nameRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = getBPCategoryTextColor(bpCategory),
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
            text = { Text(stringResource(R.string.confirm_delete_record)) },
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