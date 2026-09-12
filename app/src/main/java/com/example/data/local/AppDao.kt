package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptedQuestionEntity): Long

    @Query("SELECT * FROM attempted_questions ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<AttemptedQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgressStats(stats: ProgressStatsEntity)

    @Query("SELECT * FROM progress_stats WHERE subject = :subject LIMIT 1")
    suspend fun getProgressStats(subject: String): ProgressStatsEntity?

    @Query("SELECT * FROM progress_stats WHERE subject = :subject LIMIT 1")
    fun observeProgressStats(subject: String): Flow<ProgressStatsEntity?>

    @Query("SELECT * FROM progress_stats")
    fun observeAllProgressStats(): Flow<List<ProgressStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordSyncedQuestion(synced: SyncedQuestionEntity)

    @Query("SELECT COUNT(*) FROM synced_questions WHERE topic = :topic AND chapter = :chapter")
    suspend fun isQuestionSynced(topic: String, chapter: String): Int
}
