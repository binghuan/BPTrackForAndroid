package com.example.bptrack.data.dao

import androidx.room.*
import com.example.bptrack.data.entity.BloodPressureRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM blood_pressure_records ORDER BY dateTime DESC")
    fun getAllRecords(): Flow<List<BloodPressureRecord>>

    @Query("SELECT * FROM blood_pressure_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodPressureRecord?

    @Insert
    suspend fun insertRecord(record: BloodPressureRecord): Long

    @Update
    suspend fun updateRecord(record: BloodPressureRecord)

    @Delete
    suspend fun deleteRecord(record: BloodPressureRecord)

    @Query("DELETE FROM blood_pressure_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
} 