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
data class DiseaseImage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    val imageId: Int = 1,
    @ColumnInfo(name = "disease_id")
    val diseaseId: Int,
    @ColumnInfo(name = "image_text")
    val imageText: String,
    @ColumnInfo(name = "image_res_id")
    val imageResId: String
): Parcelable
