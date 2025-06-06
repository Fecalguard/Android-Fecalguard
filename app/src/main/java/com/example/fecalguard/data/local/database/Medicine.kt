package com.example.fecalguard.data.local.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    foreignKeys = [ForeignKey(
        entity = Disease::class,
        parentColumns = ["id"],
        childColumns = ["disease_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["disease_id"])]
)
@Parcelize
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "medicine_id")
    val medicineId: Int = 1,
    @ColumnInfo(name = "disease_id")
    val diseaseId: Int,
    @ColumnInfo(name = "medicine_name")
    val medicineName: String,
    @ColumnInfo(name = "medicine_link")
    val medicineLink: String
): Parcelable
