package com.example.data.repository

import com.example.data.StudyContentRepository
import com.example.data.local.CachedLessonDao
import com.example.data.local.CachedLessonEntity
import com.example.model.LessonPlanItem
import com.example.model.SubjectCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LessonRepository(private val dao: CachedLessonDao) {

    val allCachedLessonsFlow: Flow<List<LessonPlanItem>> = dao.getAllCachedLessonsFlow().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun ensureLessonsCachedLocally(): Int {
        val existingCount = dao.getCachedLessonCount()
        if (existingCount == 0) {
            val entities = StudyContentRepository.allLessons.map {
                CachedLessonEntity.fromDomainModel(it)
            }
            dao.insertAll(entities)
            return entities.size
        }
        return existingCount
    }

    suspend fun getCachedLesson(lessonId: String): LessonPlanItem? {
        val entity = dao.getCachedLessonById(lessonId)
        return entity?.toDomainModel() ?: StudyContentRepository.allLessons.find { it.id == lessonId }
    }

    suspend fun refreshCache(): Int {
        val entities = StudyContentRepository.allLessons.map {
            CachedLessonEntity.fromDomainModel(it)
        }
        dao.clearAll()
        dao.insertAll(entities)
        return entities.size
    }
}
