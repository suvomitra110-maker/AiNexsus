package com.example.model

enum class StreakBadge(
    val id: String,
    val title: String,
    val requiredStreak: Int,
    val iconEmoji: String,
    val description: String,
    val xpBonus: Int
) {
    DAY_1(
        id = "badge_first_spark",
        title = "First Spark",
        requiredStreak = 1,
        iconEmoji = "🌱",
        description = "Began your AI study journey on AiKIndia.com",
        xpBonus = 50
    ),
    DAY_3(
        id = "badge_3_day",
        title = "Hooghly Current",
        requiredStreak = 3,
        iconEmoji = "🌊",
        description = "Studied 3 consecutive days without missing a beat",
        xpBonus = 150
    ),
    DAY_5(
        id = "badge_5_day",
        title = "Howrah Pillar",
        requiredStreak = 5,
        iconEmoji = "🌉",
        description = "5-day unshakeable learning foundation",
        xpBonus = 250
    ),
    DAY_7(
        id = "badge_7_day",
        title = "Victoria Crest",
        requiredStreak = 7,
        iconEmoji = "🏛️",
        description = "Full 1-week streak of consecutive daily AI study",
        xpBonus = 500
    ),
    DAY_14(
        id = "badge_14_day",
        title = "Himalayan Peak",
        requiredStreak = 14,
        iconEmoji = "⛰️",
        description = "2-week high altitude AI mastery persistence",
        xpBonus = 1000
    ),
    DAY_30(
        id = "badge_30_day",
        title = "Bengal AI Scholar",
        requiredStreak = 30,
        iconEmoji = "👑",
        description = "30 consecutive days of continuous AI practice",
        xpBonus = 2500
    )
}
