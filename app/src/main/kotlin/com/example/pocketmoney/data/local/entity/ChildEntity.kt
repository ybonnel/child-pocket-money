package com.example.pocketmoney.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val colorArgb: Int,
    val weeklyAllowanceCents: Long = 0L,
    val allowanceDayOfWeek: Int = 1, // 1 = Monday (ISO)
    val allowanceActive: Boolean = true,
    val createdAtEpochMs: Long,
    val archived: Boolean = false,
)
