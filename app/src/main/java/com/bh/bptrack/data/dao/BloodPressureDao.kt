package com.bh.bptrack.data.dao

import androidx.room.*
import com.bh.bptrack.data.entity.BloodPressureRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM blood_pressure_records ORDER BY dateTime DESC")
    fun getAllRecords(): Flow<List<BloodPressureRecord>>

    @Query("SELECT * FROM blood_pressure_records ORDER BY dateTime DESC")
    suspend fun getAllRecordsSync(): List<BloodPressureRecord>

    @Query("SELECT * FROM blood_pressure_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodPressureRecord?

    @Query("SELECT * FROM blood_pressure_records WHERE date(dateTime) = date(:date)")
    suspend fun getRecordsByDate(date: String): List<BloodPressureRecord>

    @Insert
    suspend fun insertRecord(record: BloodPressureRecord): Long

    @Insert
    suspend fun insertRecords(records: List<BloodPressureRecord>): List<Long>

    @Update
    suspend fun updateRecord(record: BloodPressureRecord)

    @Delete
    suspend fun deleteRecord(record: BloodPressureRecord)

    @Query("DELETE FROM blood_pressure_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM blood_pressure_records WHERE date(dateTime) = date(:date)")
    suspend fun deleteRecordsByDate(date: String)

    @Query("DELETE FROM blood_pressure_records")
    suspend fun deleteAllRecords()
} 