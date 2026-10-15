package com.example.data.repository

import com.example.data.local.UserStreakDao
import com.example.data.local.UserStreakEntity
import com.example.model.StreakBadge
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class StreakRepository(private val dao: UserStreakDao) {

    val streakFlow: Flow<UserStreakEntity?> = dao.getStreakRecordFlow()

    suspend fun getInitialOrCurrentStreak(): UserStreakEntity {
        val existing = dao.getStreakRecord()
        if (existing != null) return existing

        val currentEpochDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        val defaultEntity = UserStreakEntity(
            id = 1,
            streakDays = 3, // Initial starter streak
            totalStudyDays = 3,
            lastStudyDateEpochDay = currentEpochDay,
            earnedBadgeIdsCsv = "${StreakBadge.DAY_1.id},${StreakBadge.DAY_3.id}"
        )
        dao.upsertStreakRecord(defaultEntity)
        return defaultEntity
    }

    /**
     * Records a study action (e.g. completing a lesson or quiz, or tapping daily check-in).
     * Calculates consecutive day logic using epoch days:
     * - Same day: streak maintained
     * - Consecutive day (last + 1): streak increments by 1
     * - Skipped > 1 day: resets streak to 1
     * Automatically evaluates and unlocks milestone badges in Room database.
     */
    suspend fun recordStudySession(): Pair<UserStreakEntity, List<StreakBadge>> {
        val current = dao.getStreakRecord() ?: getInitialOrCurrentStreak()
        val todayEpochDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        val lastDay = current.lastStudyDateEpochDay

        val newStreak = when {
            lastDay == todayEpochDay -> current.streakDays // already studied today
            todayEpochDay == lastDay + 1 -> current.streakDays + 1 // consecutive day!
            else -> 1 // missed days, restarted
        }

        val newTotalDays = if (lastDay == todayEpochDay) current.totalStudyDays else current.totalStudyDays + 1

        val currentBadgeIds = current.earnedBadgeIdsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        val newlyEarnedBadges = mutableListOf<StreakBadge>()

        StreakBadge.entries.forEach { badge ->
            if (newStreak >= badge.requiredStreak && !currentBadgeIds.contains(badge.id)) {
                currentBadgeIds.add(badge.id)
                newlyEarnedBadges.add(badge)
            }
        }

        val updatedRecord = current.copy(
            streakDays = newStreak,
            totalStudyDays = newTotalDays,
            lastStudyDateEpochDay = todayEpochDay,
            earnedBadgeIdsCsv = currentBadgeIds.joinToString(",")
        )

        dao.upsertStreakRecord(updatedRecord)
        return Pair(updatedRecord, newlyEarnedBadges)
    }

    /**
     * Demo testing helper: increments streak day for testing milestone badges
     */
    suspend fun advanceStreakDayForTesting(): Pair<UserStreakEntity, List<StreakBadge>> {
        val current = dao.getStreakRecord() ?: getInitialOrCurrentStreak()
        val nextStreak = current.streakDays + 1
        val currentBadgeIds = current.earnedBadgeIdsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        val newlyEarnedBadges = mutableListOf<StreakBadge>()

        StreakBadge.entries.forEach { badge ->
            if (nextStreak >= badge.requiredStreak && !currentBadgeIds.contains(badge.id)) {
                currentBadgeIds.add(badge.id)
                newlyEarnedBadges.add(badge)
            }
        }

        val updated = current.copy(
            streakDays = nextStreak,
            totalStudyDays = current.totalStudyDays + 1,
            lastStudyDateEpochDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()),
            earnedBadgeIdsCsv = currentBadgeIds.joinToString(",")
        )
        dao.upsertStreakRecord(updated)
        return Pair(updated, newlyEarnedBadges)
    }
}
