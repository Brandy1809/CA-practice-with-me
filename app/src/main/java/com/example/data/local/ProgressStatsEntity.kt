package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_stats")
data class ProgressStatsEntity(
    @PrimaryKey
    val subject: String,
    val questionsAttemptedCount: Int = 0,
    val questionsAnsweredCount: Int = 0,
    val questionsViewedCount: Int = 0,
    val mcqCorrectCount: Int = 0,
    val mcqAttemptedCount: Int = 0
)
