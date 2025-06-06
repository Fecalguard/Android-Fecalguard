package com.example.fecalguard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Disease::class, DiseaseImage::class, Medicine::class], version = 2, exportSchema = false)
abstract class DiseaseRoomDatabase : RoomDatabase() {
    abstract fun diseaseDao(): DiseaseDao
    abstract fun diseaseImageDao(): DiseaseImageDao
    abstract fun medicineDao(): MedicineDao
}