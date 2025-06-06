package com.example.fecalguard.data.local.database

import androidx.room.Embedded
import androidx.room.Relation

data class DiseaseWithDetails(
    @Embedded val disease: Disease,

    @Relation(
        parentColumn = "id",
        entityColumn = "diseaseId"
    )
    val images: List<DiseaseImage>,

    @Relation(
        parentColumn = "id",
        entityColumn = "diseaseId"
    )
    val medicines: List<Medicine>
)
