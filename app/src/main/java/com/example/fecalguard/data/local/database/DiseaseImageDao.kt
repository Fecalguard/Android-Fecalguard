package com.example.fecalguard.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DiseaseImageDao {
    @Transaction
    @Query("SELECT * FROM DiseaseImage WHERE disease_id = :diseaseId")
    suspend fun getDiseaseImage(diseaseId: Int): List<DiseaseImage>
}