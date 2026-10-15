package com.example.model

enum class PomodoroMode(
    val title: String,
    val defaultMinutes: Int,
    val iconEmoji: String,
    val tagLine: String
) {
    FOCUS(
        title = "AI Focus Sprint",
        defaultMinutes = 25,
        iconEmoji = "🧠",
        tagLine = "Deep focus on study modules & code"
    ),
    SHORT_BREAK(
        title = "Chai Break",
        defaultMinutes = 5,
        iconEmoji = "☕",
        tagLine = "Quick breather & Hooghly breeze"
    ),
    LONG_BREAK(
        title = "Rest & Reflection",
        defaultMinutes = 15,
        iconEmoji = "🌿",
        tagLine = "Rest eyes & review key concepts"
    )
}

data class PomodoroState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedSessionsCount: Int = 0,
    val totalFocusedMinutes: Int = 0
)
