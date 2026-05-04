package com.ybonnel.childpocketmoney.data.local.entity

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
    indices = [
        Index("childId"),
        Index("occurredAtEpochMs"),
        // Unique constraint prevents double ALLOWANCE insertion when WorkManager
        // and startup LaunchedEffect run in parallel for the same due date.
        Index(value = ["childId", "type", "occurredAtEpochMs"], unique = true),
    ]
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
