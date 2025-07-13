package com.example.bptrack.data.repository

import com.example.bptrack.data.dao.BloodPressureDao
import com.example.bptrack.data.entity.BloodPressureRecord
import kotlinx.coroutines.flow.Flow

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
} 