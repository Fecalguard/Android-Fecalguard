package com.example.fecalguard.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DiseaseDao {
    @Transaction
    @Query("SELECT * FROM Disease WHERE id = :diseaseId")
    suspend fun getDisease(diseaseId: Int): Disease
}
