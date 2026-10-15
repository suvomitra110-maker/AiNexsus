package com.example.model

enum class LeaderboardScope(val title: String, val subtitle: String, val iconEmoji: String) {
    REGIONAL_BENGAL(
        title = "Bengal & Kolkata",
        subtitle = "Local learners in Kolkata, Howrah, Darjeeling & Bengal",
        iconEmoji = "🌉"
    ),
    GLOBAL(
        title = "Global AI Circuit",
        subtitle = "Worldwide AI students on SparkAi.com",
        iconEmoji = "🌍"
    )
}

enum class LeaderboardRankingMetric(val title: String, val iconEmoji: String) {
    STREAK(title = "Study Streak", iconEmoji = "🔥"),
    QUIZ_XP(title = "Quiz Mastery XP", iconEmoji = "⚡")
}

data class LeaderboardUserEntry(
    val rank: Int,
    val name: String,
    val location: String,
    val avatarEmoji: String,
    val streakDays: Int,
    val xpSparks: Int,
    val quizAccuracyPercent: Int,
    val topBadge: String,
    val isCurrentUser: Boolean = false
)
