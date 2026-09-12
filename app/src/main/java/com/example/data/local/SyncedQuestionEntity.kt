package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_questions")
data class SyncedQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val chapter: String,
    val syncedAt: Long = System.currentTimeMillis()
)
