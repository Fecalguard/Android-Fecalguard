package com.example.fecalguard.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface MedicineDao {
    @Transaction
    @Query("SELECT * FROM Medicine WHERE disease_id = :diseaseId")
    suspend fun getMedicine(diseaseId: Int): List<Medicine>
}