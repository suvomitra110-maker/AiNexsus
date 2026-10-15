package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedLessonDao {

    @Query("SELECT * FROM cached_lesson_plans ORDER BY orderNumber ASC")
    fun getAllCachedLessonsFlow(): Flow<List<CachedLessonEntity>>

    @Query("SELECT * FROM cached_lesson_plans WHERE subjectId = :subjectId ORDER BY orderNumber ASC")
    fun getCachedLessonsBySubjectFlow(subjectId: String): Flow<List<CachedLessonEntity>>

    @Query("SELECT * FROM cached_lesson_plans WHERE id = :lessonId LIMIT 1")
    suspend fun getCachedLessonById(lessonId: String): CachedLessonEntity?

    @Query("SELECT COUNT(*) FROM cached_lesson_plans")
    suspend fun getCachedLessonCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lessons: List<CachedLessonEntity>)

    @Query("DELETE FROM cached_lesson_plans")
    suspend fun clearAll()
}
