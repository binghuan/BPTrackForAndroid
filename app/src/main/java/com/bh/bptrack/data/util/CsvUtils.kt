package com.bh.bptrack.data.util

import com.bh.bptrack.data.entity.BloodPressureRecord
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object CsvUtils {
    
    // CSV 標頭
    private const val CSV_HEADER = "Date,Systolic,Diastolic,Heartbeat,Notes"
    
    // 日期格式
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * 將血壓記錄列表匯出為 CSV 格式
     */
    fun exportToCsv(records: List<BloodPressureRecord>): String {
        val writer = StringWriter()
        
        // 寫入標頭
        writer.appendLine(CSV_HEADER)
        
        // 寫入每筆記錄
        records.forEach { record ->
            val dateStr = record.dateTime.format(DATE_FORMATTER)
            val heartbeat = record.heartRate ?: ""
            val notes = record.notes?.replace(",", "，") ?: "" // 替換逗號避免CSV格式問題
            
            writer.appendLine("$dateStr,${record.systolic},${record.diastolic},$heartbeat,$notes")
        }
        
        return writer.toString()
    }
    
    /**
     * 從 CSV 內容解析血壓記錄列表
     */
    fun importFromCsv(csvContent: String): Result<List<BloodPressureRecord>> {
        return try {
            // 清理CSV內容，移除可能的BOM和特殊字符
            val cleanedContent = csvContent
                .replace("\uFEFF", "") // 移除BOM
                .replace("__", "") // 移除用戶提到的 __ 字符
                .replace("_", "") // 移除單個下劃線
                .trim()
            
            val lines = cleanedContent.lines()
            
            if (lines.isEmpty()) {
                return Result.failure(Exception("CSV 檔案為空"))
            }
            
            // 檢查標頭
            val header = lines.first()
            if (!header.contains("Date") || !header.contains("Systolic") || !header.contains("Diastolic")) {
                return Result.failure(Exception("CSV 格式不正確，缺少必要的欄位"))
            }
            
            // 解析數據行
            val records = mutableListOf<BloodPressureRecord>()
            val errors = mutableListOf<String>()
            
            lines.drop(1).forEachIndexed { index, line ->
                val cleanedLine = line.trim()
                // 跳過空行和只有逗號的行
                if (cleanedLine.isNotEmpty() && !cleanedLine.matches(Regex("^,*$"))) {
                    parseCsvLine(cleanedLine, index + 2).fold(
                        onSuccess = { record -> records.add(record) },
                        onFailure = { error -> errors.add("第 ${index + 2} 行: ${error.message}") }
                    )
                }
            }
            
            if (errors.isNotEmpty()) {
                return Result.failure(Exception("解析錯誤:\n${errors.joinToString("\n")}"))
            }
            
            Result.success(records)
            
        } catch (e: Exception) {
            Result.failure(Exception("CSV 解析失敗: ${e.message}"))
        }
    }
    
    /**
     * 解析單行 CSV 數據
     */
    private fun parseCsvLine(line: String, lineNumber: Int): Result<BloodPressureRecord> {
        return try {
            // 先分割，然後對每個欄位單獨清理
            val parts = line.split(",").map { part ->
                part.trim()
                    .replace(Regex("^_+|_+$"), "") // 移除開頭和結尾的下劃線
                    .replace(Regex("_+"), "_") // 將多個連續下劃線替換為單個
                    .trim()
            }
            
            if (parts.size < 3) {
                return Result.failure(Exception("欄位不足，至少需要日期、收縮壓、舒張壓"))
            }
            
            // 解析日期 (預設時間為 12:00)
            val dateStr = parts[0].trim()
            
            // 更詳細的日期格式檢查
            if (dateStr.isEmpty()) {
                return Result.failure(Exception("日期欄位為空"))
            }
            
            // 檢查日期格式：必須是 yyyy/MM/dd
            if (!dateStr.matches(Regex("^\\d{4}/\\d{1,2}/\\d{1,2}$"))) {
                return Result.failure(Exception("日期格式錯誤，應為 yyyy/MM/dd，目前為: '$dateStr'"))
            }
            
            val dateTime = try {
                val dateParts = dateStr.split("/")
                val year = dateParts[0].toInt()
                val month = dateParts[1].toInt()
                val day = dateParts[2].toInt()
                
                LocalDateTime.of(year, month, day, 12, 0)
            } catch (e: Exception) {
                return Result.failure(Exception("日期格式錯誤，應為 yyyy/MM/dd，解析失敗: '$dateStr'"))
            }
            
            // 解析收縮壓
            val systolicStr = parts[1].trim()
            if (systolicStr.isEmpty()) {
                return Result.failure(Exception("收縮壓欄位為空"))
            }
            val systolic = systolicStr.toIntOrNull()
                ?: return Result.failure(Exception("收縮壓必須為數字，目前為: '$systolicStr'"))
            
            // 解析舒張壓
            val diastolicStr = parts[2].trim()
            if (diastolicStr.isEmpty()) {
                return Result.failure(Exception("舒張壓欄位為空"))
            }
            val diastolic = diastolicStr.toIntOrNull()
                ?: return Result.failure(Exception("舒張壓必須為數字，目前為: '$diastolicStr'"))
            
            // 解析心率 (可選)
            val heartRate = if (parts.size > 3 && parts[3].trim().isNotEmpty()) {
                val heartRateStr = parts[3].trim()
                heartRateStr.toIntOrNull()
                    ?: return Result.failure(Exception("心率必須為數字，目前為: '$heartRateStr'"))
            } else null
            
            // 解析備註 (可選)
            val notes = if (parts.size > 4 && parts[4].trim().isNotEmpty()) {
                val cleanedNotes = parts[4].trim()
                    .replace("，", ",") // 還原逗號
                    .replace(Regex("^_+|_+$"), "") // 移除開頭和結尾的下劃線
                    .trim()
                
                if (cleanedNotes.isNotEmpty()) cleanedNotes else null
            } else null
            
            // 驗證數據範圍
            if (systolic <= 0 || systolic > 300) {
                return Result.failure(Exception("收縮壓應在 1-300 之間，目前為: $systolic"))
            }
            
            if (diastolic <= 0 || diastolic > 200) {
                return Result.failure(Exception("舒張壓應在 1-200 之間，目前為: $diastolic"))
            }
            
            if (heartRate != null && (heartRate <= 0 || heartRate > 300)) {
                return Result.failure(Exception("心率應在 1-300 之間，目前為: $heartRate"))
            }
            
            Result.success(
                BloodPressureRecord(
                    id = 0, // 匯入時 ID 由資料庫自動生成
                    systolic = systolic,
                    diastolic = diastolic,
                    heartRate = heartRate,
                    dateTime = dateTime,
                    notes = notes
                )
            )
            
        } catch (e: Exception) {
            Result.failure(Exception("第 $lineNumber 行解析失敗: ${e.message}"))
        }
    }
    
    /**
     * 檢查兩個記錄是否為同一天 (用於去重邏輯)
     */
    fun isSameDate(record1: BloodPressureRecord, record2: BloodPressureRecord): Boolean {
        return record1.dateTime.toLocalDate() == record2.dateTime.toLocalDate()
    }
    
    /**
     * 合併記錄列表，相同日期的記錄會被覆蓋
     */
    fun mergeRecords(existingRecords: List<BloodPressureRecord>, newRecords: List<BloodPressureRecord>): List<BloodPressureRecord> {
        val mergedMap = mutableMapOf<java.time.LocalDate, BloodPressureRecord>()
        
        // 先加入現有記錄
        existingRecords.forEach { record ->
            mergedMap[record.dateTime.toLocalDate()] = record
        }
        
        // 新記錄覆蓋同日期的現有記錄
        newRecords.forEach { record ->
            mergedMap[record.dateTime.toLocalDate()] = record
        }
        
        return mergedMap.values.sortedByDescending { it.dateTime }
    }
} 