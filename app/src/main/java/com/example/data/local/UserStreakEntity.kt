package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_streak_record")
data class UserStreakEntity(
    @PrimaryKey val id: Int = 1,
    val streakDays: Int = 1,
    val totalStudyDays: Int = 1,
    val lastStudyDateEpochDay: Long = 0L,
    val earnedBadgeIdsCsv: String = "badge_first_spark,badge_3_day"
)
