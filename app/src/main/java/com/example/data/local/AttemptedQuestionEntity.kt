package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attempted_questions")
data class AttemptedQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Int,
    val topic: String,
    val questionText: String,
    val userAnswerText: String,
    val subject: String,
    val chapter: String,
    val mode: String,
    val isCorrect: Boolean? = null,
    val aiMarksAwarded: Int? = null,
    val aiFeedback: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
