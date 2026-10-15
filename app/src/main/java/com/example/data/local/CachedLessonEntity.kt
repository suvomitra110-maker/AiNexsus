package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LessonPlanItem
import com.example.model.ProficiencyLevel
import com.example.model.SubjectCategory

@Entity(tableName = "cached_lesson_plans")
data class CachedLessonEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val minLevelName: String,
    val orderNumber: Int,
    val title: String,
    val summary: String,
    val durationMinutes: Int,
    val xpReward: Int,
    val keyTakeawaysCsv: String,
    val studyContentMarkdown: String,
    val interactiveTip: String,
    val formulaOrSnippet: String? = null,
    val cachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): LessonPlanItem {
        val subject = SubjectCategory.entries.find { it.id == subjectId } ?: SubjectCategory.AI_FUNDAMENTALS
        val level = try {
            ProficiencyLevel.valueOf(minLevelName)
        } catch (_: Exception) {
            ProficiencyLevel.NOVICE
        }
        val takeaways = keyTakeawaysCsv.split("|||").filter { it.isNotBlank() }

        return LessonPlanItem(
            id = id,
            subject = subject,
            minLevel = level,
            orderNumber = orderNumber,
            title = title,
            summary = summary,
            durationMinutes = durationMinutes,
            xpReward = xpReward,
            keyTakeaways = takeaways,
            studyContentMarkdown = studyContentMarkdown,
            interactiveTip = interactiveTip,
            formulaOrSnippet = formulaOrSnippet
        )
    }

    companion object {
        fun fromDomainModel(item: LessonPlanItem): CachedLessonEntity {
            return CachedLessonEntity(
                id = item.id,
                subjectId = item.subject.id,
                minLevelName = item.minLevel.name,
                orderNumber = item.orderNumber,
                title = item.title,
                summary = item.summary,
                durationMinutes = item.durationMinutes,
                xpReward = item.xpReward,
                keyTakeawaysCsv = item.keyTakeaways.joinToString("|||"),
                studyContentMarkdown = item.studyContentMarkdown,
                interactiveTip = item.interactiveTip,
                formulaOrSnippet = item.formulaOrSnippet
            )
        }
    }
}
