package com.bh.bptrack.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bh.bptrack.data.converter.DateTimeConverter
import com.bh.bptrack.data.dao.BloodPressureDao
import com.bh.bptrack.data.entity.BloodPressureRecord

@Database(
    entities = [BloodPressureRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class BloodPressureDatabase : RoomDatabase() {
    abstract fun bloodPressureDao(): BloodPressureDao

    companion object {
        @Volatile
        private var INSTANCE: BloodPressureDatabase? = null

        fun getDatabase(context: Context): BloodPressureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodPressureDatabase::class.java,
                    "blood_pressure_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 