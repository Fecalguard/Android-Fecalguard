package com.example.fecalguard.data.local.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Disease (
    @ColumnInfo(name = "disease_name")
    val diseaseName: String,
    @ColumnInfo(name = "symptoms")
    val symptoms: String,
    @ColumnInfo(name = "side_effects")
    val sideEffects: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "long_term_solution")
    val longTermSolution: String,
    @ColumnInfo(name = "quick_treatment")
    val quickTreatment: String,
): Parcelable
