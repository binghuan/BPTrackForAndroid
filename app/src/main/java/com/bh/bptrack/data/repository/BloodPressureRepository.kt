package com.bh.bptrack.data.repository

import com.bh.bptrack.data.dao.BloodPressureDao
import com.bh.bptrack.data.entity.BloodPressureRecord
import com.bh.bptrack.data.util.CsvUtils
import kotlinx.coroutines.flow.Flow
import java.time.format.DateTimeFormatter

class BloodPressureRepository(private val dao: BloodPressureDao) {
    
    fun getAllRecords(): Flow<List<BloodPressureRecord>> {
        return dao.getAllRecords()
    }
    
    suspend fun getRecordById(id: Long): BloodPressureRecord? {
        return dao.getRecordById(id)
    }
    
    suspend fun insertRecord(record: BloodPressureRecord): Long {
        return dao.insertRecord(record)
    }
    
    suspend fun updateRecord(record: BloodPressureRecord) {
        dao.updateRecord(record)
    }
    
    suspend fun deleteRecord(record: BloodPressureRecord) {
        dao.deleteRecord(record)
    }
    
    suspend fun deleteRecordById(id: Long) {
        dao.deleteRecordById(id)
    }
    
    // CSV 匯出功能
    suspend fun exportToCsv(): Result<String> {
        return try {
            val records = dao.getAllRecordsSync()
            val csvContent = CsvUtils.exportToCsv(records)
            Result.success(csvContent)
        } catch (e: Exception) {
            Result.failure(Exception("匯出失敗: ${e.message}"))
        }
    }
    
    // CSV 匯入功能，包含去重邏輯
    suspend fun importFromCsv(csvContent: String): Result<Int> {
        return try {
            // 解析 CSV 內容
            val parseResult = CsvUtils.importFromCsv(csvContent)
            if (parseResult.isFailure) {
                return Result.failure(parseResult.exceptionOrNull()!!)
            }
            
            val newRecords = parseResult.getOrThrow()
            if (newRecords.isEmpty()) {
                return Result.failure(Exception("沒有有效的記錄需要匯入"))
            }
            
            // 獲取現有記錄
            val existingRecords = dao.getAllRecordsSync()
            
            // 找出需要覆蓋的記錄（相同日期）
            val recordsToDelete = mutableListOf<BloodPressureRecord>()
            newRecords.forEach { newRecord ->
                existingRecords.forEach { existingRecord ->
                    if (CsvUtils.isSameDate(newRecord, existingRecord)) {
                        recordsToDelete.add(existingRecord)
                    }
                }
            }
            
            // 刪除相同日期的舊記錄
            recordsToDelete.forEach { record ->
                dao.deleteRecord(record)
            }
            
            // 插入新記錄
            dao.insertRecords(newRecords)
            
            Result.success(newRecords.size)
            
        } catch (e: Exception) {
            Result.failure(Exception("匯入失敗: ${e.message}"))
        }
    }
    
    // 獲取指定日期的記錄
    suspend fun getRecordsByDate(date: String): List<BloodPressureRecord> {
        return dao.getRecordsByDate(date)
    }
    
    // 清除所有記錄
    suspend fun deleteAllRecords() {
        dao.deleteAllRecords()
    }
} 