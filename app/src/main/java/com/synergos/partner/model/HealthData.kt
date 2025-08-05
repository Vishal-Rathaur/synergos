package com.synergos.partner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_data")
data class HealthData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val value: Double,
    val timestamp: Long
)


