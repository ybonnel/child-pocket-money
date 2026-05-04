package com.example.pocketmoney.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = ChildEntity::class,
        parentColumns = ["id"],
        childColumns = ["childId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("childId"), Index("occurredAtEpochMs")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val childId: Long,
    val amountCents: Long,  // signed: positive = credit, negative = debit
    val label: String,
    val type: String,       // TransactionType.name
    val occurredAtEpochMs: Long,
    val createdAtEpochMs: Long,
)
